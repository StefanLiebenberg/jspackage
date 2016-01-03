package org.slieb.jspackage.compile.providers;

import org.slieb.jspackage.compile.nodes.SingleCompileNode;
import org.slieb.jspackage.compile.result.CompileResult;
import org.slieb.jspackage.compile.tasks.Task;
import slieb.kute.Kute;
import slieb.kute.KuteDigest;
import slieb.kute.api.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;


public class SingleCompileResourceProvider implements Resource.Provider {

    private List<Resource.Readable> cachedResources;
    private byte[] lastChecksum;

    private final Task<SingleCompileNode, CompileResult> compileTask;

    private final SingleCompileNode compileNode;

    public SingleCompileResourceProvider(Task<SingleCompileNode, CompileResult> compileTask, SingleCompileNode compileNode) {
        this.compileTask = compileTask;
        this.compileNode = compileNode;
    }


    @Override
    public Stream<Resource.Readable> stream() {
        byte[] checksum = getSourcesChecksum();
        if (cachedResources == null || lastChecksum == null || !Arrays.equals(lastChecksum, checksum)) {
            cachedResources = compileStream().map(castFunctionWithThrowable(Kute::immutableMemoryResource)).collect(toList());
            lastChecksum = checksum;
        }
        return cachedResources.stream();
    }

    public Stream<Resource.Readable> compileStream() {
        CompileResult compileResult = compileTask.perform(compileNode);
        switch (compileResult.getType()) {
            case SUCCESS:
                return new SuccessResourceProvider((CompileResult.Success) compileResult).stream();
            default:
                return Stream.empty();
        }
    }

    protected byte[] getSourcesChecksum() {
        return KuteDigest.md5(compileNode.getSourcesProvider());
    }
}
