package com.dbtool.utils;

public class Pair<F, S> {
	private F firstItem;
	private S secondItem;
	
	public Pair(F firstItem, S secondItem) {
		this.firstItem = firstItem;
		this.secondItem = secondItem;
	}
	
	public F first() {
		return firstItem;
	}
	
	public S second() {
		return secondItem;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstItem == null) ? 0 : firstItem.hashCode());
		result = prime * result
				+ ((secondItem == null) ? 0 : secondItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?,?> other = (Pair<?,?>) obj;
		if (firstItem == null) {
			if (other.firstItem != null)
				return false;
		} else if (!firstItem.equals(other.firstItem))
			return false;
		if (secondItem == null) {
			if (other.secondItem != null)
				return false;
		} else if (!secondItem.equals(other.secondItem))
			return false;
		return true;
	}

}
