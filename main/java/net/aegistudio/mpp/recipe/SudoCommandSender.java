package net.aegistudio.mpp.recipe;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 * Sudo command sender is a sender that is able to execute 
 * any command, by its permissible setting to true.
 * 
 * @author aegistudio
 */

public class SudoCommandSender implements CommandSender {
	public final CommandSender sender;
	public SudoCommandSender(CommandSender wrapped) {
		this.sender = wrapped;
	}
	
	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return this.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return this.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return this.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return this.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return this.sender.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return this.sender.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return this.sender.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		this.sender.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		this.sender.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return this.sender.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		// Never
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public Server getServer() {
		return sender.getServer();
	}

	@Override
	public void sendMessage(String arg0) {
		sender.sendMessage(arg0);
	}

	@Override
	public void sendMessage(String[] arg0) {
		this.sender.sendMessage(arg0);
	}

}
