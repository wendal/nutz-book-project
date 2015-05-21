package net.wendal.nutzbook.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.wendal.nutzbook.bean.User;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

/**
 * Git仓库管理
 * @author wendal
 *
 */
@At("/admin/git")
@IocBean(create="init")
@Ok("json")
public class GitAdminModule extends BaseModule {
	
	@Inject 
	protected PropertiesProxy conf;
	protected File root;
	private static final Log log = Logs.get();
	
	public void init(){
		root = new File(conf.get("git.root", "/data/nutzbook/git"));
		root = Files.createDirIfNoExists(root);
		log.debug("git.root = " + root.getAbsolutePath());
	}

	@RequiresUser
	@At
	public Object list(@Attr("me")long userId) {
		User me = dao.fetch(User.class, userId);
		File userGitHome = new File(root, me.getName());
		if (!userGitHome.exists()) {
			return ajaxOk(Collections.EMPTY_LIST);
		}
		List<NutMap> repos = new ArrayList<NutMap>();
		for (File dir :userGitHome.listFiles()) {
			if (!dir.isDirectory())
				continue;
			File _git = new File(dir, ".git");
			if (_git.exists() && _git.isDirectory()) {
				NutMap repo = new NutMap();
				repo.setv("name", dir.getName());
				if (new File(_git, "repo_public").exists()) {
					repo.setv("public", true);
				} else {
					repo.setv("public", false);
				}
				repos.add(repo);
			}
		}
		return ajaxOk(repos);
	}
	
	@At
	@RequiresUser
	public Object create(@Attr("me")long userId, @Param("name")String name) {
		// TODO 限制数量
		User me = dao.fetch(User.class, userId);
		if (Strings.isBlank(name) || !name.matches("[a-zA-Z0-9\\-_]{3,20}")) {
			return ajaxFail("名字不合法");
		}
		File gitDir = new File(root, me.getName() + "/" + name);
		if (gitDir.exists() && gitDir.list().length != 0) {
			return ajaxFail("该Git库已经存在");
		}
		Files.createDirIfNoExists(gitDir);
		boolean re = execute(gitDir, "git", "init") 
				&& execute(gitDir, "git", "config", "http.receivepack", "true")
				&& execute(gitDir, "git", "config", "receive.denyCurrentBranch", "ignore");
		if (re) {
			return ajaxOk("done");
		}
		return ajaxFail("服务器内部错误");
	}
	
	@At
	@RequiresUser
	public Object fix(@Attr("me")long userId, @Param("name")String name) {
		User me = dao.fetch(User.class, userId);
		if (Strings.isBlank(name) || !name.matches("[a-zA-Z0-9\\-_]{3,20}")) {
			return ajaxFail("名字不合法");
		}
		File gitDir = new File(root, me.getName() + "/" + name);
		boolean re = execute(gitDir, "git", "config", "http.receivepack", "true")
				  && execute(gitDir, "git", "config", "receive.denyCurrentBranch", "ignore");
		if (re) {
			return ajaxOk("done");
		}
		return ajaxFail("失败");
	}
	
	@At
	@RequiresUser
	public Object delete(@Attr("me")long userId, @Param("name")String name) {
		User me = dao.fetch(User.class, userId);
		if (Strings.isBlank(name) || !name.matches("[a-zA-Z0-9\\-_]{3,20}")) {
			return ajaxFail("名字不合法");
		}
		File gitDir = new File(root, me.getName() + "/" + name);
		Files.clearDir(gitDir);
		gitDir.delete();
		return ajaxOk(null);
	}
	
	@At("/update/public")
	@RequiresUser
	public Object asPublic(@Attr("me")long userId, @Param("name")String name, @Param("public")boolean flag) {
		User me = dao.fetch(User.class, userId);
		if (Strings.isBlank(name) || !name.matches("[a-zA-Z0-9\\-_]{3,20}")) {
			return ajaxFail("名字不合法");
		}
		File _gitDir = new File(root, me.getName() + "/" + name + "/.git");
		if (!_gitDir.exists()) {
			return ajaxFail("没有这个repo");
		}
		File tag = new File(_gitDir, "repo_public");
		if (flag)
			Files.createFileIfNoExists(tag);
		else
			tag.delete();
		return ajaxOk(null);
	}
	
	
	protected boolean execute(File dir, String...cmdarray) {
		try {
			Runtime.getRuntime().exec(cmdarray, new String[]{}, dir).waitFor();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
