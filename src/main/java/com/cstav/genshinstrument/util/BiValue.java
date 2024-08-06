package com.cstav.genshinstrument.util;

import org.jetbrains.annotations.NotNull;

public record BiValue<T1, T2>(@NotNull T1 obj1, @NotNull T2 obj2) {

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BiValue<?, ?> bv)
            && bv.obj1.equals(obj1)
            && bv.obj2.equals(obj2);
    }

}
