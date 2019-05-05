/*
 * Copyright 2016-2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.cli.cmd.state.pkg;

import java.io.IOException;
import java.util.Set;
import org.aesh.command.CommandDefinition;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.cli.CommandExecutionException;
import org.jboss.galleon.cli.HelpDescriptions;
import org.jboss.galleon.cli.PmCommandInvocation;
import org.jboss.galleon.cli.cmd.CliErrors;
import org.jboss.galleon.cli.cmd.CommandDomain;
import org.jboss.galleon.cli.model.state.State;
import org.jboss.galleon.config.FeaturePackConfig;

/**
 *
 * @author jdenise@redhat.com
 */
@CommandDefinition(name = "remove-excluded-package", description = HelpDescriptions.REMOVE_EXCLUDED_PACKAGE, activator = ProvisionedPackageCommandActivator.class)
public class StateRemoveExcludedPackageCommand extends AbstractProvisionedPackageCommand {

    @Override
    protected void runCommand(PmCommandInvocation invoc, State session, FeaturePackConfig config) throws IOException, ProvisioningException, CommandExecutionException {
        try {
            session.removeExcludedPackage(invoc.getPmSession(), PackagesUtil.getExcludedPackages(invoc.getPmSession(), config, getPackage()));
        } catch (Exception ex) {
            throw new CommandExecutionException(invoc.getPmSession(), CliErrors.removeFailed(), ex);
        }
    }

    @Override
    protected Set<String> getTargetedPackages(FeaturePackConfig cf) {
        return cf.getExcludedPackages();
    }

    @Override
    public CommandDomain getDomain() {
        return CommandDomain.EDITING;
    }
}
