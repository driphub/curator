package org.apache.curator.x.async.details;

import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.imps.CreateBuilderImpl;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.x.async.AsyncCreateBuilder;
import org.apache.curator.x.async.AsyncPathable;
import org.apache.curator.x.async.AsyncStage;
import org.apache.curator.x.async.CreateOption;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.curator.x.async.details.BackgroundProcs.pathProc;
import static org.apache.curator.x.async.details.BackgroundProcs.safeCall;

class AsyncCreateBuilderImpl implements AsyncCreateBuilder
{
    private final CuratorFrameworkImpl client;
    private final UnhandledErrorListener unhandledErrorListener;
    private CreateMode createMode = CreateMode.PERSISTENT;
    private List<ACL> aclList = null;
    private Set<CreateOption> options = Collections.emptySet();
    private Stat stat = null;

    AsyncCreateBuilderImpl(CuratorFrameworkImpl client, UnhandledErrorListener unhandledErrorListener)
    {
        this.client = client;
        this.unhandledErrorListener = unhandledErrorListener;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> storingStatIn(Stat stat)
    {
        this.stat = stat;
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withMode(CreateMode createMode)
    {
        this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withACL(List<ACL> aclList)
    {
        this.aclList = aclList;
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withOptions(Set<CreateOption> options)
    {
        this.options = Objects.requireNonNull(options, "options cannot be null");
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withOptions(Set<CreateOption> options, List<ACL> aclList)
    {
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.aclList = aclList;
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withOptions(Set<CreateOption> options, CreateMode createMode, List<ACL> aclList)
    {
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.aclList = aclList;
        this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withOptions(Set<CreateOption> options, CreateMode createMode)
    {
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
        return this;
    }

    @Override
    public AsyncPathable<AsyncStage<String>> withOptions(Set<CreateOption> options, CreateMode createMode, List<ACL> aclList, Stat stat)
    {
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.aclList = aclList;
        this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
        this.stat = stat;
        return this;
    }

    @Override
    public AsyncStage<String> forPath(String path)
    {
        return internalForPath(path, null, false);
    }

    @Override
    public AsyncStage<String> forPath(String path, byte[] data)
    {
        return internalForPath(path, data, true);
    }

    private AsyncStage<String> internalForPath(String path, byte[] data, boolean useData)
    {
        BuilderCommon<String> common = new BuilderCommon<>(unhandledErrorListener, false, pathProc);
        CreateBuilderImpl builder = new CreateBuilderImpl(client,
            createMode,
            common.backgrounding,
            options.contains(CreateOption.createParentsIfNeeded),
            options.contains(CreateOption.createParentsAsContainers),
            options.contains(CreateOption.doProtected),
            options.contains(CreateOption.compress),
            options.contains(CreateOption.setDataIfExists),
            aclList,
            stat
        );
        return safeCall(common.internalCallback, () -> useData ? builder.forPath(path, data) : builder.forPath(path));
    }
}
