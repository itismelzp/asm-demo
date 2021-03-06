package com.walker.analytics.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SensorsAnalyticsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        AppExtension appExtension = project.extensions.findByType(AppExtension.class)
        appExtension.registerTransform(new SensorsAnalyticsTransform(project))
    }
}