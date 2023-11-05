package net.aegistudio.mpp.script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.script.ScriptEngine;

public enum Token {
    UNDEFINED {
        @Override
        public Object parse(DataInputStream input, ScriptEngine engine) {	return null;	}

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) {	}
    },
    BOOLEAN {
        @Override
        public Boolean parse(DataInputStream input, ScriptEngine engine) throws IOException {
            return input.readBoolean();
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeBoolean((boolean)((Boolean)value));
        }
    },
    INTEGER {
        @Override
        public Integer parse(DataInputStream input, ScriptEngine engine) throws IOException {
            return input.readInt();
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeInt((int)((Integer)value));
        }
    },
    LONG {
        @Override
        public Long parse(DataInputStream input, ScriptEngine engine) throws IOException {
            return input.readLong();
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeLong((long)((Long)value));
        }
    },
    DOUBLE {
        @Override
        public Double parse(DataInputStream input, ScriptEngine engine) throws Exception {
            return input.readDouble();
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeDouble((double)((Double)value));
        }
    },
    STRING {
        @Override
        public String parse(DataInputStream input, ScriptEngine engine) throws Exception {
            return input.readUTF();
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeUTF(value.toString());
        }
    },
    FUNCTION {
        @Override
        public Object parse(DataInputStream input, ScriptEngine engine) throws Exception {
            String function = input.readUTF();
            return engine.eval(function);
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            output.writeUTF(value.toString());
        }
    },
    COMPOSITE {
        @Override
        public Map<String, Object> parse(DataInputStream input, ScriptEngine engine) throws Exception {
            Map<String, Object> composite = new TreeMap<String, Object>();
            int size = input.readInt();
            for(int i = 0; i < size; i ++) {
                String key = input.readUTF();
                byte token = input.readByte();
                Object value = values()[token].parse(input, engine);
                composite.put(key, value);
            }
            return composite;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception {
            Map<String, Object> composite = (Map<String, Object>)value;
            output.writeInt(composite.size());
            for(Entry<String, Object> entry : composite.entrySet()) {
                output.writeUTF(entry.getKey());
                Token token = what(entry.getValue(), engine);
                output.writeByte(token.ordinal());
                token.persist(output, engine, entry.getValue());
            }
        }
    },
    ARRAY {
        @Override
        public Object parse(DataInputStream input, ScriptEngine engine) throws Exception {
            ArrayToken arrayToken = ARRAY_TOKEN.get(engine.getFactory().getEngineName());
            Object result = arrayToken.create(engine);

            long size = input.readLong();
            for(long i = 0; i < size; i ++) {
                byte token = input.readByte();
                Object current = values()[token].parse(input, engine);
                arrayToken.add(engine, result, current);
            }
            return result;
        }

        @Override
        public void persist(DataOutputStream output, ScriptEngine engine, Object array) throws Exception {
            ArrayToken arrayToken = ARRAY_TOKEN.get(engine.getFactory().getEngineName());
            long size = arrayToken.length(engine, array);
            output.writeLong(size);

            for(int i = 0; i < size; i ++) {
                Object current = arrayToken.index(engine, array, i);
                Token token = what(current, engine);
                output.writeByte(token.ordinal());
                token.persist(output, engine, current);
            }
        }
    };

    public abstract Object parse(DataInputStream input, ScriptEngine engine) throws Exception;

    public abstract void persist(DataOutputStream output, ScriptEngine engine, Object value) throws Exception;

    // Engine based.
    public static final TreeMap<String, String> IS_FUNCTION = new TreeMap<String, String>();
    public static final TreeMap<String, String> IS_ARRAY = new TreeMap<String, String>();

    public static final TreeMap<String, ArrayToken> ARRAY_TOKEN = new TreeMap<String, ArrayToken>();

    static {
        String NASHORN = "Oracle Nashorn";
        IS_FUNCTION.put(NASHORN, "isFunction");
        IS_ARRAY.put(NASHORN, "isArray");
        ARRAY_TOKEN.put(NASHORN, new EcmaArrayToken());

        String RHINO = "Oracle Rhino";
        IS_FUNCTION.put(RHINO, "isFunction");
        IS_ARRAY.put(RHINO, "isArray");
        ARRAY_TOKEN.put(RHINO, new EcmaArrayToken());
    }

    public static Token what(Object obj, ScriptEngine engine) {
        if(obj == null) return UNDEFINED;
        if(obj instanceof Boolean) return BOOLEAN;
        if(obj instanceof Integer) return INTEGER;
        if(obj instanceof Long) return LONG;
        if(obj instanceof String) return STRING;
        if(obj instanceof Double) return DOUBLE;

        String engineName = engine.getFactory().getEngineName();
        try {
            if((boolean)obj.getClass().getMethod(IS_FUNCTION.get(engineName)).invoke(obj))
                return FUNCTION;
        } catch(Throwable t) {}

        try {
            if((boolean)obj.getClass().getMethod(IS_ARRAY.get(engineName)).invoke(obj))
                return ARRAY;
        } catch(Throwable t) {}

        if(obj instanceof Map) return COMPOSITE;
        return UNDEFINED;
    }
}

