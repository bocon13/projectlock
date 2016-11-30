package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.common.ProjectUtil;
import com.google.gerrit.reviewdb.client.Branch;
import com.google.gerrit.reviewdb.client.Project;
import com.google.gerrit.server.project.NoSuchProjectException;
import com.google.gerrit.server.project.ProjectControl;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * FIXME.
 */
public class BranchKeyHandler extends OptionHandler<Branch.NameKey> {
    private static final Logger log = LoggerFactory.getLogger(BranchKeyHandler.class);

    @Inject
    public BranchKeyHandler(
            @Assisted final CmdLineParser parser, @Assisted final OptionDef option,
            @Assisted final Setter<Branch.NameKey> setter) {
        super(parser, option, setter);
    }

    @Override
    public final int parseArguments(final Parameters params)
            throws CmdLineException {
        String branchName = params.getParameter(0);

        setter.addValue(null);
        return 1;
    }

    @Override
    public final String getDefaultMetaVariable() {
        return "PROJECT";
    }
}

