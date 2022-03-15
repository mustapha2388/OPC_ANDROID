package com.cleanup.todoc.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cleanup.todoc.database.AppDatabase;
import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.database.model.Project;

import java.util.List;

public class ProjectRepository {

    private final ProjectDao projectDao;
    private final Project[] allProjects;

    public ProjectRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        projectDao = db.projectDao();
        allProjects = projectDao.getAllProjects();
    }

    public Project[] getAllProjects() {
        return allProjects;
    }

    public void insertAll(List<Project> projects) {
        projectDao.insertAll(projects);
    }

    public void insert(Project project) {
        projectDao.insert(project);
    }

}
