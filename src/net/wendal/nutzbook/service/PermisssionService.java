package net.wendal.nutzbook.service;

import net.wendal.nutzbook.bean.Role;

public interface PermisssionService {

	void initFormPackage(String pkg);
	
	public void addPermission(String permission);
	
	public Role addRole(String role);
}
