package com.github.notanelephant.codingbuddyplugin.notifications

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object NotificationHelper {
    fun showNotification(project: Project,content: String, type: NotificationType = NotificationType.INFORMATION, title: String = "Coding Buddy") {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Basic Notification Group")
            .createNotification(
                title,
                content,
                type,
            )
            .notify(project)
    }
}