package com.cstav.genshinstrument.networking.buttonidentifier;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class NoteButtonIdentifiers {
    private static final HashSet<Class<? extends NoteButtonIdentifier>> ACCEPTABLE_IDENTIFIERS = new HashSet<>();

    public static Collection<Class<? extends NoteButtonIdentifier>> getAcceptableIdentifiers() {
        return Collections.unmodifiableCollection(ACCEPTABLE_IDENTIFIERS);
    }

    public static void register(final Class<? extends NoteButtonIdentifier> identifierType) {
        ACCEPTABLE_IDENTIFIERS.add(identifierType);
    }
    @SafeVarargs
    public static void register(final Class<? extends NoteButtonIdentifier>... identifierTypes) {
        for (final Class<? extends NoteButtonIdentifier> identifierType : identifierTypes) {
            register(identifierType);
        }
    }


    /**
     * Gets a {@link NoteButtonIdentifier} as described by the {@code classType} destination.
     * Will only return a class type if it is valid and included in the {@code acceptableIdentifiers} list.
     * @param classType The class name of the requested identifiers
     *
     * @return The class of the requested identifier
     * @throws ClassNotFoundException If the requested class was not found in the provided {@code acceptableIdentifiers} list
     */
    public static Class<? extends NoteButtonIdentifier> getIdentifier(String classType) throws ClassNotFoundException {

        for (final Class<? extends NoteButtonIdentifier> identifier : getAcceptableIdentifiers()) {
            if (identifier.getName().equals(classType))
                return identifier;
        }

        throw new ClassNotFoundException("Class type "+classType+" could not be evaluated as part of an acceptable identifier");
    }
}
