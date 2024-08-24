package com.cstav.genshinstrument.util;

import java.util.Arrays;

public record MultTuple(Object... objects) {

    @Override
    public boolean equals(Object o) {
        return (this == o) || (
            (o instanceof MultTuple other)
            && Arrays.equals(objects, other.objects)
        );
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(objects);
    }
}
