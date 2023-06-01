// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.tis.integreen.carsharingbzit.api;

public class BoundingBox {
	private Point geoPosWS;
	private Point geoPosEN;

	
	public BoundingBox() {
	}
	
	public BoundingBox(double left, double top, double right, double bottom) {
		this.geoPosWS = new Point(left,bottom);
		this.geoPosEN = new Point(right,top);
	}
	public Point getGeoPosWS() {
		return geoPosWS;
	}
	public void setGeoPosWS(Point geoPosWS) {
		this.geoPosWS = geoPosWS;
	}
	public Point getGeoPosEN() {
		return geoPosEN;
	}
	public void setGeoPosEN(Point geoPosEN) {
		this.geoPosEN = geoPosEN;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geoPosEN == null) ? 0 : geoPosEN.hashCode());
		result = prime * result
				+ ((geoPosWS == null) ? 0 : geoPosWS.hashCode());
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
		BoundingBox other = (BoundingBox) obj;
		if (geoPosEN == null) {
			if (other.geoPosEN != null)
				return false;
		} else if (!geoPosEN.equals(other.geoPosEN))
			return false;
		if (geoPosWS == null) {
			if (other.geoPosWS != null)
				return false;
		} else if (!geoPosWS.equals(other.geoPosWS))
			return false;
		return true;
	}
}
