package com.ilyasov.model;

import java.util.Random;

public class Department {
    private int id;
    private String depCode;
    private String depJob;
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getDepJob() {
        return depJob;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + depCode.hashCode();
        result = 31 * result + depJob.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Department)) {
            return false;
        }

        Department department = (Department) o;

        return department.depCode.equals(depCode) &&
                department.description.equals(description) &&
                department.depJob.equals(depJob);
    }
}
