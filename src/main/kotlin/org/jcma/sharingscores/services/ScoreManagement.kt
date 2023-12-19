package org.jcma.sharingscores.services

import org.jcma.sharingscores.models.Instrument
import org.jcma.sharingscores.models.Score
import org.jcma.sharingscores.models.ScoreBlock

class ScoreManagement(
  private val gCloudSharing: GCloudSharing,
  private val instruments: List<Instrument>
) {
  fun shareScore(scoreUrl: String): Score {
    val gFiles = gCloudSharing.getFilesInFolder(scoreUrl)
    val parentId = gCloudSharing.extractFolderIdFromDriveLink(scoreUrl)!!
    val score =
      Score(
        "Score",
        instruments.map { instrument ->
          val regex = Regex(instrument.name)

          val file = gFiles.find { regex.containsMatchIn(it.name) }
          val updatedUrl =
            if (file != null) {
              if (gCloudSharing.shareFile(file.webViewLink)) {
                file.webViewLink
              } else null
            } else null
          ScoreBlock(instrument.restrictLevel, instrument.publishName, updatedUrl)
        }
      )
    gCloudSharing.createFile(score, parentId)
    return score
  }
}
