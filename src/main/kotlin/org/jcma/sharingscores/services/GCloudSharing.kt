package org.jcma.sharingscores.services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.Permission
import java.io.File
import org.jcma.sharingscores.models.GCloudFiles
import org.jcma.sharingscores.models.Score

class GCloudSharing(private val service: Drive) : Sharing {

  private val permission = Permission().setType("anyone").setRole("reader")

  override fun getFilesInFolder(folderUrl: String): List<GCloudFiles> {
    val folderId = extractFolderIdFromDriveLink(folderUrl)
    val files =
      try {
        service
          .files()
          .list()
          .setQ("'$folderId' in parents")
          .setFields("files(id, name, webViewLink)")
          .execute()
          .files
          ?: emptyList()
      } catch (e: Exception) {
        println("Error getting files: $e")
        emptyList()
      }
    return files.mapNotNull { file ->
      GCloudFiles(id = file.id, name = file.name, webViewLink = file.webViewLink)
    }
  }

  fun createFile(content: Score, parentId: String) {
    val fileMetadata = com.google.api.services.drive.model.File()
    fileMetadata.name = content.name // "MyReport.txt"
    fileMetadata.parents = listOf(parentId)

    // Create a temporary file and write some data to it
    val tempFile = java.io.File.createTempFile(content.name, ".txt")
    tempFile.writeText(content.blocks.joinToString(separator = "\n") { it.toString() })

    val mediaContent = FileContent("text/plain", tempFile)

    service.files().create(fileMetadata, mediaContent).setFields("id").execute()
  }

  override fun shareFile(fileUrl: String): Boolean {
    val folderId = extractFolderIdFromDriveLink(fileUrl)
    return try {
      service.permissions().create(folderId, permission).execute() != null
    } catch (e: Exception) {
      println("Error sharing file: $e")
      false
    }
  }

  fun extractFolderIdFromDriveLink(driveLink: String): String? {
    // Define a regular expression pattern to extract the folder ID
    val pattern =
      if (driveLink.contains("folders")) {
        Regex("/folders/([\\w-]+)")
      } else {
        Regex("/file/d/([\\w-]+)")
      }

    // Find the first match in the driveLink
    val matchResult = pattern.find(driveLink)

    // Check if a match was found and return the folder ID
    return matchResult?.groups?.get(1)?.value
  }

  companion object {
    private const val APPLICATION_NAME: String = "Sharing Scores"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private val SCOPES: List<String> =
      listOf(DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)

    fun create(
      tokensDirectoryPath: String,
      clientId: String,
      authUri: String,
      tokenUri: String,
      clientSecret: String
    ): GCloudSharing {
      val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
      val service: Drive =
        Drive.Builder(
            httpTransport,
            JSON_FACTORY,
            getCredentials(
              httpTransport,
              tokensDirectoryPath,
              clientId,
              authUri,
              tokenUri,
              clientSecret
            )
          )
          .setApplicationName(APPLICATION_NAME)
          .build()
      return GCloudSharing(service)
    }

    private fun getCredentials(
      httpTransport: NetHttpTransport,
      tokensDirectoryPath: String,
      clientId: String,
      authUri: String,
      tokenUri: String,
      clientSecret: String
    ): Credential {

      val details =
        GoogleClientSecrets.Details()
          .setClientId(clientId)
          .setAuthUri(authUri)
          .setTokenUri(tokenUri)
          .setClientSecret(clientSecret)
      val clientSecrets = GoogleClientSecrets().set("web", details)

      // Build flow and trigger user authorization request.
      val flow =
        GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
          .setDataStoreFactory(FileDataStoreFactory(File(tokensDirectoryPath)))
          .setAccessType("offline")
          .build()
      val receiver = LocalServerReceiver.Builder().setPort(8888).build()
      return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
  }
}
