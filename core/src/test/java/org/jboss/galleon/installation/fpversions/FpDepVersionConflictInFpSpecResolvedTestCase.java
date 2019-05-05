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
package org.jboss.galleon.installation.fpversions;

import org.jboss.galleon.universe.galleon1.LegacyGalleon1Universe;
import org.jboss.galleon.universe.FeaturePackLocation.FPID;
import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.config.FeaturePackConfig;
import org.jboss.galleon.config.ProvisioningConfig;
import org.jboss.galleon.creator.FeaturePackCreator;
import org.jboss.galleon.spec.PackageDependencySpec;
import org.jboss.galleon.state.ProvisionedFeaturePack;
import org.jboss.galleon.state.ProvisionedPackage;
import org.jboss.galleon.state.ProvisionedState;
import org.jboss.galleon.test.PmProvisionConfigTestBase;
import org.jboss.galleon.test.util.fs.state.DirState;

/**
 *
 * @author Alexey Loubyansky
 */
public class FpDepVersionConflictInFpSpecResolvedTestCase extends PmProvisionConfigTestBase {

    private static final FPID FP9_100_GAV = LegacyGalleon1Universe.newFPID("org.jboss.pm.test:fp9", "1", "1.0.0.Final");
    private static final FPID FP1_100_GAV = LegacyGalleon1Universe.newFPID("org.jboss.pm.test:fp1", "1", "1.0.0.Final");
    private static final FPID FP1_101_GAV = LegacyGalleon1Universe.newFPID("org.jboss.pm.test:fp1", "1", "1.0.1.Final");
    private static final FPID FP2_200_GAV = LegacyGalleon1Universe.newFPID("org.jboss.pm.test:fp2", "2", "2.0.0.Final");
    private static final FPID FP3_100_GAV = LegacyGalleon1Universe.newFPID("org.jboss.pm.test:fp3", "1", "1.0.0.Final");

    @Override
    protected void createFeaturePacks(FeaturePackCreator creator) throws ProvisioningException {
        creator
            .newFeaturePack(FP1_100_GAV)
                .newPackage("p1", true)
                    .addDependency(PackageDependencySpec.optional("p2"))
                    .writeContent("fp1/p1.txt", "fp1 1.0.0.Final p1")
                    .getFeaturePack()
                .newPackage("p2")
                    .writeContent("fp1/p2.txt", "fp1 1.0.0.Final p2")
                    .getFeaturePack()
                .newPackage("p3")
                    .writeContent("fp1/p3.txt", "fp1 1.0.0.Final p3")
                    .getFeaturePack()
                .getCreator()
            .newFeaturePack(FP1_101_GAV)
                .newPackage("p1", true)
                    .addDependency(PackageDependencySpec.optional("p2"))
                    .writeContent("fp1/p1.txt", "fp1 1.0.1.Final p1")
                    .getFeaturePack()
                .newPackage("p2")
                    .writeContent("fp1/p2.txt", "fp1 1.0.1.Final p2")
                    .getFeaturePack()
                .newPackage("p3")
                    .writeContent("fp1/p3.txt", "fp1 1.0.1.Final p3")
                    .getFeaturePack()
                .getCreator()
            .newFeaturePack(FP2_200_GAV)
                .addDependency(FP1_100_GAV.getLocation())
                .newPackage("p1", true)
                    .writeContent("fp2/p1.txt", "fp2 p1")
                    .getFeaturePack()
                .getCreator()
            .newFeaturePack(FP3_100_GAV)
                .addDependency(FP1_101_GAV.getLocation())
                .newPackage("p1", true)
                    .writeContent("fp3/p1.txt", "fp3 p1")
                    .getFeaturePack()
                .getCreator()
            .newFeaturePack(FP9_100_GAV)
                .addDependency(FeaturePackConfig.forLocation(FP1_101_GAV.getLocation()))
                .addDependency(FeaturePackConfig.forLocation(FP2_200_GAV.getLocation()))
                .addDependency(FeaturePackConfig.forLocation(FP3_100_GAV.getLocation()))
                .newPackage("p1", true)
                    .writeContent("fp9/p1.txt", "fp9 p1")
                    .getFeaturePack()
                .getCreator()
            .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig()
            throws ProvisioningDescriptionException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.forLocation(FP9_100_GAV.getLocation()))
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningDescriptionException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(FP1_101_GAV)
                        .addPackage(ProvisionedPackage.newInstance("p1"))
                        .addPackage(ProvisionedPackage.newInstance("p2"))
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP2_200_GAV)
                        .addPackage(ProvisionedPackage.newInstance("p1"))
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP3_100_GAV)
                        .addPackage(ProvisionedPackage.newInstance("p1"))
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(FP9_100_GAV)
                        .addPackage(ProvisionedPackage.newInstance("p1"))
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("fp1/p1.txt", "fp1 1.0.1.Final p1")
                .addFile("fp1/p2.txt", "fp1 1.0.1.Final p2")
                .addFile("fp2/p1.txt", "fp2 p1")
                .addFile("fp3/p1.txt", "fp3 p1")
                .addFile("fp9/p1.txt", "fp9 p1")
                .build();
    }
}
