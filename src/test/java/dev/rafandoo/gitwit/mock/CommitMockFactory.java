package dev.rafandoo.gitwit.mock;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.mockito.Mockito;

/**
 * Factory class for creating mock RevCommit objects for testing purposes.
 */
public class CommitMockFactory {

    /**
     * Creates a mock RevCommit with the specified SHA and commit message.
     *
     * @param sha     the SHA string to identify the commit
     * @param message the full commit message
     * @return a mocked {@link RevCommit} object
     */
    public static RevCommit mockCommit(String sha, String message) {
        RevCommit commit = Mockito.mock(RevCommit.class);
        ObjectId objectId = ObjectId.fromString(
            String.format("%040x", sha.hashCode())
        );
        Mockito.when(commit.getId()).thenReturn(objectId);
        Mockito.when(commit.getFullMessage()).thenReturn(message);
        return commit;
    }
}
