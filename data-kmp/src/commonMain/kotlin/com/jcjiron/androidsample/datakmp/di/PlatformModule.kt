package com.jcjiron.androidsample.datakmp.di

import org.koin.core.module.Module

/**
 * `expect`: "cada plataforma me tiene que dar esto". Las versiones `actual` están en
 * androidMain (OkHttp + base de datos con Context) e iosMain (Darwin + carpeta de documentos).
 */
internal expect val platformModule: Module
