package org.jcma.sharingscores.models

import kotlinx.serialization.Serializable

@Serializable
data class Score(val name: String, val blocks: List<ScoreBlock>, val videoUrl: String? = null) {
  override fun toString(): String {
    val videoBlock =
      if (videoUrl != null) {
        """href="$videoUrl" target="_blank" rel="noopener""""
      } else ""
    val header = listOf("""<h3><strong><a $videoBlock>$name</a><br></strong></h3>""")
    return (header + blocks).joinToString(separator = "\n") { it.toString() }
  }
}

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
