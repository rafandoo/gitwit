package dev.rafandoo.gitwit.service.changelog.render;

import dev.rafandoo.gitwit.entity.Changelog;

/**
 * Renderer interface for rendering changelogs.
 */
public interface Renderer {

    /**
     * Renders the given changelog.
     *
     * @param changelog the changelog to render.
     * @param append    whether to append to existing content.
     * @return the rendered changelog as a string.
     */
    String render(Changelog changelog, boolean append);

}
