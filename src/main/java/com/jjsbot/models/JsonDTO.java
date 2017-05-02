package com.jjsbot.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JsonDTO {
    @JsonProperty("nodes")
    private List<Person> nodes;
    @JsonProperty("links")
    private List<Link> links;
    public JsonDTO(List nodes,List links){
        this.nodes = nodes;
        this.links = links;
    }
    public List<Person> getNodes() {
        return nodes;
    }

    public void setNodes(List<Person> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
