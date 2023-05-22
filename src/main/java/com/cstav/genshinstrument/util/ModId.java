package com.cstav.genshinstrument.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotator used to indicate the mod ID of the class
 */
@Target(ElementType.TYPE)
public @interface ModId {

    String modid();
    
}