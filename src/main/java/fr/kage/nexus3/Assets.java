package fr.kage.nexus3;

import lombok.Data;

import java.util.Collection;

@Data
public class Assets {

	private Collection<Item> items;
	private String continuationToken;

}
