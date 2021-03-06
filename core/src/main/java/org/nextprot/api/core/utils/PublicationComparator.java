package org.nextprot.api.core.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Publication;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * Base class for comparing Publications in ascending order of String field
 */
public abstract class PublicationComparator implements Comparator<Publication> {

    private final Function<Publication, String> toFieldStringFunc;

    private PublicationComparator(Function<Publication, String> toFieldStringFunc) {

        Preconditions.checkNotNull(toFieldStringFunc);
        this.toFieldStringFunc = toFieldStringFunc;
    }

    public static PublicationComparator StringComparator(Function<Publication, String> toFieldStringFunc) {

        return new StringComparator(toFieldStringFunc);
    }

    public static PublicationComparator FormattedNumberComparator(Function<Publication, String> toFieldStringFunc) {

        return new IntComparator(toFieldStringFunc);
    }

    @Override
    public int compare(Publication p1, Publication p2) {

        String stringField1 = toFieldStringFunc.apply(p1);
        String stringField2 = toFieldStringFunc.apply(p2);

        if (Objects.equals(stringField1, stringField2) ) {
            return 0;
        }

        if (stringField1 == null || stringField1.isEmpty()) {
            return 1;
        }

        if (stringField2 == null || stringField2.isEmpty()) {
            return -1;
        }

        return compareType(stringField1, stringField2);
    }

    protected abstract int compareType(String str1, String str2);

    public static class StringComparator extends PublicationComparator {

        public StringComparator(Function<Publication, String> toFieldStringFunc) {

            super(toFieldStringFunc);
        }

        @Override
        protected int compareType(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }

    public static class IntComparator extends PublicationComparator {

        public IntComparator(Function<Publication, String> toFieldStringFunc) {

            super(toFieldStringFunc);
        }

        @Override
        protected int compareType(String str1, String str2) {

            // TODO: Data to fix - some publications have a huge number as the first page and last page empty
            // Here is an example of a publication with this problem: pub id=48965432, start_page="1010428317698354", end_page=""
            boolean isInt1 = str1.matches("\\d+") && str1.length()<7;
            boolean isInt2 = str2.matches("\\d+") && str2.length()<7;

            // compare ints if both string are formatted numbers
            if (isInt1 && isInt2) {

                return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
            }

            else if (isInt1) {

                return -1;
            }

            else if (isInt2) {

                return 1;
            }

            // compare strings
            return str1.compareTo(str2);
        }
    }
}
