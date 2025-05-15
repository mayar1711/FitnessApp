package com.example.fitnessapp.di

import com.example.fitnessapp.model.datasource.local.HealthLocalDataSource
import com.example.fitnessapp.model.datasource.local.HealthLocalDataSourceImp
import com.example.fitnessapp.model.repository.HealthRepository
import com.example.fitnessapp.model.repository.HealthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class RepoModule {
    @Binds
    abstract fun bindRepo(impl: HealthRepositoryImpl): HealthRepository

    @Binds
    abstract fun bindLocalDataSource(impl: HealthLocalDataSourceImp): HealthLocalDataSource


}