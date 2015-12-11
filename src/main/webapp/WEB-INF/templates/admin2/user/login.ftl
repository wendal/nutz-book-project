<#import "../layout/simple.ftl" as layout>
<@layout.admin2SimpleLayout title="${msg['user.login.title']}">
        <!-- Widget starts -->
            <div class="widget worange">
              <!-- Widget head -->
              <div class="widget-head">
                <i class="fa fa-lock"></i> Login 
              </div>

              <div class="widget-content">
                <div class="padd">
                  <!-- Login form -->
                  <form class="form-horizontal">
                    <!-- Email -->
                    <div class="form-group">
                      <label class="control-label col-lg-3" for="inputUsername">${msg['user.login.username']}</label>
                      <div class="col-lg-9">
                        <input type="text" class="form-control" id="inputUsername" placeholder="Username">
                      </div>
                    </div>
                    <!-- Password -->
                    <div class="form-group">
                      <label class="control-label col-lg-3" for="inputPassword">${msg['user.login.password']}</label>
                      <div class="col-lg-9">
                        <input type="password" class="form-control" id="inputPassword" placeholder="Password">
                      </div>
                    </div>
                    <!-- Remember me checkbox and sign in button -->
                    <div class="form-group">
					<div class="col-lg-9 col-lg-offset-3">
                      <div class="checkbox">
                        <label>
                          <input type="checkbox"> ${msg['user.login.rememberme']}
                        </label>
						</div>
					</div>
					</div>
                        <div class="col-lg-9 col-lg-offset-3">
							<button type="submit" class="btn btn-info btn-sm">Sign in</button>
							<button type="reset" class="btn btn-default btn-sm">Reset</button>
						</div>
                    <br />
                  </form>
				  
				</div>
			  </div>
              
            <div class="widget-foot">
              ${msg['user.register.not_registred']}? <a href="register.html">${msg['user.register.register_now']}</a>
            </div>
          </div>
</@layout.admin2SimpleLayout>