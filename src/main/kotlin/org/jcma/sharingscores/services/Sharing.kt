package org.jcma.sharingscores.services

import org.jcma.sharingscores.models.GCloudFiles

interface Sharing {
    fun getFilesInFolder(folderUrl: String): List<GCloudFiles>
    fun shareFile(fileUrl: String): Boolean
}
