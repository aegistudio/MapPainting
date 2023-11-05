package net.aegistudio.mpp.canvas;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.aegistudio.mpp.MapPainting;

public class CanvasMppOutputStream extends OutputStream implements AutoCloseable{

    private final OutputStream outputStream;
    public CanvasMppOutputStream(OutputStream output) {
        this.outputStream = output;
    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }

    @Override
    public void write(int arg0) throws IOException {
        this.outputStream.write(arg0);
    }

    public void writeCanvas(MapPainting painting, Canvas canvas) throws Exception {
        DataOutputStream dout = new DataOutputStream(outputStream);
        this.writeHeader();
        this.writeClass(canvas);
        dout.flush();

        canvas.save(painting, outputStream);
    }

    public void writeHeader() throws IOException {
        DataOutputStream dout = new DataOutputStream(outputStream);

        dout.writeByte('P');
        dout.writeByte('P');
        dout.writeByte('M');
    }

    public void writeClass(Canvas canvas) throws IOException {
        DataOutputStream dout = new DataOutputStream(outputStream);
        dout.writeUTF(canvas.getClass().getName());
    }
}