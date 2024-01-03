package org.jcma.sharingscores.services

import org.jcma.sharingscores.models.GCloudFiles
import org.jcma.sharingscores.models.Instrument
import org.jcma.sharingscores.models.Score
import org.jcma.sharingscores.models.ScoreBlock

class ScoreManagement(
  private val gCloudSharing: GCloudSharing,
  private val instruments: List<Instrument>
) {
  fun shareScore(scoreUrl: String, videoUrl: String?): Score {
    val gFiles = gCloudSharing.getFilesInFolder(scoreUrl)
    val parentId = gCloudSharing.extractFolderIdFromDriveLink(scoreUrl)!!
    val name = gCloudSharing.getName(scoreUrl)
    val score =
      Score(name = name, blocks = shareAndFilter(instruments, gFiles), videoUrl = videoUrl)
    gCloudSharing.createFile(score, parentId)
    return score
  }

  private fun shareAndFilter(
    instruments: List<Instrument>,
    gFiles: List<GCloudFiles>
  ): List<ScoreBlock> {
    return gFiles
      .flatMap { gFile ->
        gCloudSharing.shareFile(gFile.webViewLink)
        instruments.map { instrument ->
          val regex = Regex(instrument.name)
          if (regex.containsMatchIn(gFile.name)) {
            ScoreBlock(instrument.restrictLevel, instrument.publishName, gFile.webViewLink)
          } else null
        }
      }
      .filterNotNull()
  }

  private fun oldShareAndFilter(
    instruments: List<Instrument>,
    gFiles: List<GCloudFiles>
  ): List<ScoreBlock> {
    return instruments.map { instrument ->
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
  }
}
