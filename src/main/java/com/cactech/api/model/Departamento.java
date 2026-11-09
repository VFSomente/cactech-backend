package com.cactech.api.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Departamentos da loja. O slug e o identificador usado nas URLs e no front-end.
 */
public enum Departamento {
    COMPUTADORES("computadores", "Computadores"),
    KIT_UPGRADE("kit-upgrade", "Kits Upgrade"),
    HARDWARE("hardware", "Hardware"),
    MONITORES("monitores", "Monitores");

    private final String slug;
    private final String nome;

    Departamento(String slug, String nome) {
        this.slug = slug;
        this.nome = nome;
    }

    public String getSlug() {
        return slug;
    }

    public String getNome() {
        return nome;
    }

    public static Optional<Departamento> fromSlug(String slug) {
        return Arrays.stream(values())
                .filter(d -> d.slug.equalsIgnoreCase(slug))
                .findFirst();
    }
}
