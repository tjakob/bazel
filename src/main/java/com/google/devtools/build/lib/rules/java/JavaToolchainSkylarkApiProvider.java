// Copyright 2015 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.java;

import static com.google.common.base.StandardSystemProperty.JAVA_SPECIFICATION_VERSION;

import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.analysis.skylark.SkylarkApiProvider;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec;
import com.google.devtools.build.lib.skylarkbuildapi.java.JavaToolchainSkylarkApiProviderApi;
import com.google.devtools.build.lib.syntax.SkylarkList;
import com.google.devtools.build.lib.syntax.SkylarkNestedSet;
import java.util.Iterator;

/**
 * A class that exposes the java_toolchain providers to Skylark. It is intended to provide a simple
 * and stable interface for Skylark users.
 */
@AutoCodec
public final class JavaToolchainSkylarkApiProvider extends SkylarkApiProvider
    implements JavaToolchainSkylarkApiProviderApi {
  /** The name of the field in Skylark used to access this class. */
  public static final String NAME = "java_toolchain";

  /** Returns the input Java language level */
  // TODO(cushon): remove this API; it bakes a deprecated detail of the javac API into Bazel
  @Override
  public String getSourceVersion() {
    JavaToolchainProvider javaToolchainProvider = JavaToolchainProvider.from(getInfo());
    Iterator<String> it = javaToolchainProvider.getJavacOptions().iterator();
    while (it.hasNext()) {
      if (it.next().equals("-source") && it.hasNext()) {
        return it.next();
      }
    }
    return JAVA_SPECIFICATION_VERSION.value();
  }

  /** Returns the target Java language level */
  // TODO(cushon): remove this API; it bakes a deprecated detail of the javac API into Bazel
  @Override
  public String getTargetVersion() {
    JavaToolchainProvider javaToolchainProvider = JavaToolchainProvider.from(getInfo());
    Iterator<String> it = javaToolchainProvider.getJavacOptions().iterator();
    while (it.hasNext()) {
      if (it.next().equals("-target") && it.hasNext()) {
        return it.next();
      }
    }
    return JAVA_SPECIFICATION_VERSION.value();
  }

  /** Returns the {@link Artifact} of the javac jar */
  @Override
  public Artifact getJavacJar() {
    JavaToolchainProvider javaToolchainProvider = JavaToolchainProvider.from(getInfo());
    return javaToolchainProvider.getJavac();
  }

  /** Returns the {@link Artifact} of the SingleJar deploy jar */
  @Override
  public Artifact getSingleJar() {
    return JavaToolchainProvider.from(getInfo()).getSingleJar();
  }

  /** Returns the bootclass path entries */
  @Override
  public SkylarkNestedSet getBootclasspath() {
    return SkylarkNestedSet.of(
        Artifact.class, JavaToolchainProvider.from(getInfo()).getBootclasspath());
  }

  /** Returns the JVM options */
  @Override
  public SkylarkList<String> getJvmOptions() {
    return SkylarkList.createImmutable(JavaToolchainProvider.from(getInfo()).getJvmOptions());
  }

  /** Returns the compilation tools */
  @Override
  public SkylarkNestedSet getTools() {
    return SkylarkNestedSet.of(Artifact.class, JavaToolchainProvider.from(getInfo()).getTools());
  }
}
