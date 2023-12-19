package org.jcma.sharingscores.models

import kotlinx.serialization.Serializable

@Serializable data class Score(val name: String, val blocks: List<ScoreBlock>)

@Serializable
class ScoreBlock(
  private val restrictLevel: String,
  private val name: String,
  private val url: String?
) {

  override fun toString(): String {
    val link =
      if (url != null) {
        "href=\"$url\""
      } else ""

    return """
<!-- wp:html -->
[restrict level="$restrictLevel"]
<!-- /wp:html -->

<!-- wp:paragraph -->
<p><a $link target="_blank" rel="noreferrer noopener">$name</a></p>
<!-- /wp:paragraph -->

<!-- wp:html -->
[/restrict]
<!-- /wp:html -->
    """
      .trimMargin()
  }
}
