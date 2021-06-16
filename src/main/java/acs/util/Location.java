//package acs.util;
//
//import javax.persistence.Embeddable;
//
//@Embeddable
//public class Location {
//
//	private Double lat;
//	private Double lng;
//
//	public Location() {
//	}
//
//	public Location(Double d, Double e) {
//		super();
//		this.lat = d;
//		this.lng = e;
//	}
//
//	public Double getLat() {
//		return lat;
//	}
//
//	public void setLat(Double lat) {
//		if (lat != null) {
//			this.lat = lat;
//		}
//	}
//
//	public Double getLng() {
//		return lng;
//	}
//
//	public void setLng(Double lng) {
//		if (lng != null) {
//			this.lng = lng;
//		}
//	}
//
//	public void validation() {
//
//		if (lat == null) {
//			throw new RuntimeException("latitude was not instantiate");
//		}
//
//		if (lng == null) {
//			throw new RuntimeException("longitude was not instantiate");
//		}
//	}
//
//}
