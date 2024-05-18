package com.cstav.genshinstrument.util;

import org.jetbrains.annotations.NotNull;

public record BiValue<T1, T2>(@NotNull T1 obj1, @NotNull T2 obj2) {
//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof BiValue<?, ?> other))
//            return false;
//
//        if (other.obj1 == null)
//            return obj1 == null;
//        if (other.obj2 == null)
//            return obj2 == null;
//
//        return other.obj1.equals(obj1)
//            && other.obj2.equals(obj2);
//    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BiValue<?, ?> bv)
            && bv.obj1.equals(obj1)
            && bv.obj2.equals(obj2);
    }
}
