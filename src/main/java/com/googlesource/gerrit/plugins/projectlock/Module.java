package com.googlesource.gerrit.plugins.projectlock;

import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.server.git.validators.MergeValidationListener;
import com.google.inject.AbstractModule;

class Module extends AbstractModule {
  @Override
  protected void configure() {
      DynamicSet.bind(binder(), MergeValidationListener.class)
              .to(ProjectLockedValidator.class);
  }
}
