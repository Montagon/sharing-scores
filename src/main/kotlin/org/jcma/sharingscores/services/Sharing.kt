package org.jcma.sharingscores.services

interface Sharing {
    fun getFilesInFolder(folderUrl: String): List<String>
    fun shareFile(fileUrl: String): Boolean
}
