<#import "../layout/simple.ftl" as layout>
<@layout.admin2SimpleLayout title="${msg['user.register.title']}">
        <!-- Widget starts -->
            <div class="widget wred">
              <div class="widget-head">
                <i class="fa fa-lock"></i> Register 
              </div>
              <div class="widget-content">
                <div class="padd">
                  
                  <form class="form-horizontal">
                    <!-- Registration form starts -->
                                      <!-- Name -->
                                          <div class="form-group">
                                            <label class="control-label col-lg-3" for="name">Name</label>
                                            <div class="col-lg-9">
                                              <input type="text" class="form-control" id="name">
                                            </div>
                                          </div>   
                                          <!-- Email -->
                                          <div class="form-group">
                                            <label class="control-label col-lg-3" for="email">Email</label>
                                            <div class="col-lg-9">
                                              <input type="text" class="form-control" id="email">
                                            </div>
                                          </div>
                                          <!-- Select box -->
                                          <div class="form-group">
                                            <label class="control-label col-lg-3">Drop Down</label>
                                            <div class="col-lg-4">                               
                                                <select class="form-control">
                                                <option>&nbsp;</option>
                                                <option>1</option>
                                                <option>2</option>
                                                <option>3</option>
                                                <option>4</option>
                                                <option>5</option>
                                                </select>  
                                            </div>
                                          </div>                                           
                                          <!-- Username -->
                                          <div class="form-group">
                                            <label class="control-label col-lg-3" for="username">Username</label>
                                            <div class="col-lg-9">
                                              <input type="text" class="form-control" id="username">
                                            </div>
                                          </div>
                                          <!-- Password -->
                                          <div class="form-group">
                                            <label class="control-label col-lg-3" for="email">Password</label>
                                            <div class="col-lg-9">
                                              <input type="password" class="form-control" id="password">
                                            </div>
                                          </div>
                                          <!-- Accept box and button s-->
                                          <div class="form-group">
                                            <div class="col-lg-9 col-lg-offset-3">
											  <div class="checkbox">
                                              <label>
                                                <input type="checkbox"> Accept Terms &amp; Conditions
                                              </label>
											  </div>
                                              <br />
                                              <button type="submit" class="btn btn-sm btn-info">Register</button>
                                              <button type="reset" class="btn btn-sm btn-default">Reset</button>
                                            </div>
                                          </div>
                  </form>

                </div>
              </div>
                <div class="widget-foot">
                  Already Registred? <a href="login.html">Login</a>
                </div>
            </div>
</@layout.admin2SimpleLayout>