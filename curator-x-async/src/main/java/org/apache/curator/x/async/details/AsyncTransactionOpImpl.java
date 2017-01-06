package org.apache.curator.x.async.details;

import org.apache.curator.framework.api.ACLCreateModePathAndBytesable;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.curator.framework.api.VersionPathAndBytesable;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.framework.api.transaction.TransactionSetDataBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.x.async.AsyncPathAndBytesable;
import org.apache.curator.x.async.AsyncPathable;
import org.apache.curator.x.async.AsyncTransactionCheckBuilder;
import org.apache.curator.x.async.AsyncTransactionCreateBuilder;
import org.apache.curator.x.async.AsyncTransactionDeleteBuilder;
import org.apache.curator.x.async.AsyncTransactionOp;
import org.apache.curator.x.async.AsyncTransactionSetDataBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import java.util.Objects;

class AsyncTransactionOpImpl implements AsyncTransactionOp
{
    private final CuratorFrameworkImpl client;

    AsyncTransactionOpImpl(CuratorFrameworkImpl client)
    {
        this.client = client;
    }

    @Override
    public AsyncTransactionCreateBuilder create()
    {
        return new AsyncTransactionCreateBuilder()
        {
            private List<ACL> aclList = null;
            private CreateMode createMode = CreateMode.PERSISTENT;
            private boolean compressed = false;

            @Override
            public AsyncPathable<CuratorOp> withMode(CreateMode createMode)
            {
                this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
                return this;
            }

            @Override
            public AsyncPathable<CuratorOp> withACL(List<ACL> aclList)
            {
                this.aclList = aclList;
                return this;
            }

            @Override
            public AsyncPathable<CuratorOp> compressed()
            {
                compressed = true;
                return this;
            }

            @Override
            public AsyncPathable<CuratorOp> withOptions(CreateMode createMode, List<ACL> aclList, boolean compressed)
            {
                this.createMode = Objects.requireNonNull(createMode, "createMode cannot be null");
                this.aclList = aclList;
                this.compressed = compressed;
                return this;
            }

            @Override
            public CuratorOp forPath(String path, byte[] data)
            {
                return internalForPath(path, data, true);
            }

            @Override
            public CuratorOp forPath(String path)
            {
                return internalForPath(path, null, false);
            }

            private CuratorOp internalForPath(String path, byte[] data, boolean useData)
            {
                TransactionCreateBuilder<CuratorOp> builder1 = client.transactionOp().create();
                ACLCreateModePathAndBytesable<CuratorOp> builder2 = compressed ? builder1.compressed() : builder1;
                PathAndBytesable<CuratorOp> builder3 = builder2.withACL(aclList);
                try
                {
                    return useData ? builder3.forPath(path, data) : builder3.forPath(path);
                }
                catch ( Exception e )
                {
                    throw new RuntimeException(e);  // should never happen
                }
            }
        };
    }

    @Override
    public AsyncTransactionDeleteBuilder delete()
    {
        return new AsyncTransactionDeleteBuilder()
        {
            private int version = -1;

            @Override
            public AsyncPathable<CuratorOp> withVersion(int version)
            {
                this.version = version;
                return this;
            }

            @Override
            public CuratorOp forPath(String path)
            {
                try
                {
                    return client.transactionOp().delete().withVersion(version).forPath(path);
                }
                catch ( Exception e )
                {
                    throw new RuntimeException(e);  // should never happen
                }
            }
        };
    }

    @Override
    public AsyncTransactionSetDataBuilder setData()
    {
        return new AsyncTransactionSetDataBuilder()
        {
            private int version = -1;
            private boolean compressed = false;

            @Override
            public AsyncPathAndBytesable<CuratorOp> withVersion(int version)
            {
                this.version = version;
                return this;
            }

            @Override
            public AsyncPathAndBytesable<CuratorOp> compressed()
            {
                compressed = true;
                return this;
            }

            @Override
            public AsyncPathAndBytesable<CuratorOp> withVersionCompressed(int version)
            {
                this.version = version;
                compressed = true;
                return this;
            }

            @Override
            public CuratorOp forPath(String path, byte[] data)
            {
                return internalForPath(path, data, true);
            }

            @Override
            public CuratorOp forPath(String path)
            {
                return internalForPath(path, null, false);
            }

            private CuratorOp internalForPath(String path, byte[] data, boolean useData)
            {
                TransactionSetDataBuilder<CuratorOp> builder1 = client.transactionOp().setData();
                VersionPathAndBytesable<CuratorOp> builder2 = compressed ? builder1.compressed() : builder1;
                PathAndBytesable<CuratorOp> builder3 = builder2.withVersion(version);
                try
                {
                    return useData ? builder3.forPath(path, data) : builder3.forPath(path);
                }
                catch ( Exception e )
                {
                    throw new RuntimeException(e);  // should never happen
                }
            }
        };
    }

    @Override
    public AsyncTransactionCheckBuilder check()
    {
        return new AsyncTransactionCheckBuilder()
        {
            private int version = -1;

            @Override
            public AsyncPathable<CuratorOp> withVersion(int version)
            {
                this.version = version;
                return this;
            }

            @Override
            public CuratorOp forPath(String path)
            {
                try
                {
                    return client.transactionOp().check().withVersion(version).forPath(path);
                }
                catch ( Exception e )
                {
                    throw new RuntimeException(e);  // should never happen
                }
            }
        };
    }
}
