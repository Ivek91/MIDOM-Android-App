package hr.fer.zari.midom.model;

import java.util.List;

public class Specialisation {

    private int id;
    private String name;
    private String parentStudy;
    private List<Integer> subStudies;
    private boolean selected;

    public Specialisation(int id, String name, String parentStudy, List<Integer> subStudies) {
        this.id = id;
        this.name = name;
        this.parentStudy = parentStudy;
        this.subStudies = subStudies;
    }

    public String getParentStudy() {
        return parentStudy;
    }

    public void setParentStudy(String parentStudy) {
        this.parentStudy = parentStudy;
    }

    public List<Integer> getSubStudies() {
        return subStudies;
    }

    public void setSubStudies(List<Integer> subStudies) {
        this.subStudies = subStudies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Specialisation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentStudy='" + parentStudy + '\'' +
                ", subStudies='" + subStudies + '\'' +
                '}';
    }
}
