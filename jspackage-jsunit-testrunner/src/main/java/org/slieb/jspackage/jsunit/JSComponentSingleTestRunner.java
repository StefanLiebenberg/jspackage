package org.slieb.jspackage.jsunit;


import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import slieb.kute.api.Resource;
import slieb.kute.api.ResourceProvider;

public class JSComponentSingleTestRunner extends Runner {

    private final ResourceProvider<Resource.Readable> resourceProvider;

    private final String htmlPath;

    public JSComponentSingleTestRunner(ResourceProvider<Resource.Readable> resourceProvider, String htmlPath) {
        this.resourceProvider = resourceProvider;
        this.htmlPath = htmlPath;
    }

    public Description getDescription() {
        String path = htmlPath;
        String name = path.replace("_test.html", "").replaceAll("\\.", "_").replaceAll("/", ".");
        return Description.createSuiteDescription(name, path);
    }

    @Override
    public void run(RunNotifier notifier) {
        // todo:
        // provision server or get from parent.
        // provision webdriver or get from parent.
        // point webdriver to htmlPath and run jsunit testing on runtime
        // if provisioned server, kill it
        // if provisioned webdriver, kill it
        throw new RuntimeException("Not Implemented Yet");
    }

}
