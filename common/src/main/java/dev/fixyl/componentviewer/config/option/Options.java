package dev.fixyl.componentviewer.config.option;

public interface Options {

    // Suppress the generic wildcard warning for SonarQube
    // since it's an array of mixed types and the actual
    // type doesn't matter
    @SuppressWarnings("java:S1452")
    public AdvancedOption<?>[] getOptions();
}
