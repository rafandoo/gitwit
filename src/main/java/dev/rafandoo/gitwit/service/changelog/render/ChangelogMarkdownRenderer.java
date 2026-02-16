package dev.rafandoo.gitwit.service.changelog.render;

import com.google.inject.Inject;
import dev.rafandoo.cup.utils.StringUtils;
import dev.rafandoo.gitwit.entity.Changelog;
import dev.rafandoo.gitwit.service.I18nService;
import dev.rafandoo.gitwit.util.EmojiUtil;
import lombok.AllArgsConstructor;
import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.text.heading.Heading;

/**
 * Markdown renderer for changelogs.
 */
@AllArgsConstructor(onConstructor_ = @__({@Inject}))
public class ChangelogMarkdownRenderer implements Renderer {

    private final I18nService i18nService;

    private static final String NL = "\n\n";

    @Override
    public String render(Changelog changelog, boolean append) {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isNullOrBlank(changelog.title()) && !append) {
            Heading heading = new Heading(
                EmojiUtil.processEmojis(changelog.title()),
                1
            );
            heading.setUnderlineStyle(false);
            sb.append(heading).append(NL);
        }

        if (!StringUtils.isNullOrBlank(changelog.subtitle())) {
            Heading subtitleHeading = new Heading(
                EmojiUtil.processEmojis(changelog.subtitle()),
                2
            );
            subtitleHeading.setUnderlineStyle(false);
            sb.append(subtitleHeading).append(NL);
        }

        if (!changelog.breakingChanges().isEmpty()) {
            sb.append(new Heading("Breaking Changes", 3)).append(NL);
            sb.append(new UnorderedList<>(changelog.breakingChanges())).append(NL);
        }

        changelog.sections()
            .forEach((title, items) -> {
                sb.append(new Heading(title, 3)).append(NL);
                sb.append(new UnorderedList<>(items)).append(NL);
            });

        if (!changelog.otherChanges().isEmpty()) {
            sb.append(new Heading(this.i18nService.resolve("changelog.other"), 3)).append(NL);
            sb.append(new UnorderedList<>(changelog.otherChanges()));
        }

        return sb.toString();
    }
}
