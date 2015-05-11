package ch.epfl.lia.util;

import static ch.epfl.lia.util.FileUtils.foreachLine;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import ch.epfl.lia.entity.Article;
import ch.epfl.lia.entity.Language;

/**
 * @author Cyriaque Brousse
 */
public final class Articles {
    
    private Articles() { }

    /**
     * @param fileName
     *            the file to deserialize from
     * @return the disk-stored article if it was found, {@code null} otherwise
     */
    public static Article getFromDisk(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            Object obj = Serialization.deserialize(fileName);
            return (Article) obj;
        } else {
            return null;
        }
    }

    /**
     * @param articleId
     *            the id - will search for a file matching raw/'id'.txt
     * @param language
     *            the language of the article
     * @return the constructed article. The creation date will be the current
     *         system time, and the title will be "N/A".
     * @throws IOException
     *             if the file could not be found or an error happened
     */
    public static Article constructArticleFromRaw(int articleId, Language language) throws IOException {
        Preconditions.throwIfNull("need a language", language);
        if (articleId < 0) {
            throw new IllegalArgumentException("article id must be positive");
        }
        
        String title = "N/A";
        Timestamp creationDate = new Timestamp(System.currentTimeMillis());
        
        StringBuilder builder = new StringBuilder();
        final String fileName = "raw/" + articleId + ".txt";
        foreachLine(fileName, true, line -> builder.append(line + "\n"));
        String contents = builder.toString();
        
        return new Article(articleId, language, null, null, title, contents, creationDate, null);
    }

}
