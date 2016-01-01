package org.slieb.jspackage.compile.tasks;

@FunctionalInterface
public interface Task<Configuration, Result> {

    Result perform(Configuration node);

}
