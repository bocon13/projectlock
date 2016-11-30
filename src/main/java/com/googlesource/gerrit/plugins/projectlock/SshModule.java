package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.sshd.PluginCommandModule;
import com.googlesource.gerrit.plugins.projectlock.commands.LockCommand;
import com.googlesource.gerrit.plugins.projectlock.commands.LockListCommand;
import com.googlesource.gerrit.plugins.projectlock.commands.UnlockCommand;

public class SshModule extends PluginCommandModule {
    @Override
    protected void configureCommands() {
        command(LockCommand.class);
        command(UnlockCommand.class);
        command(LockListCommand.class);
    }
}
