<!DOCTYPE html>
<html lang="en">
<head>
<title>${codeName}</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Jquery组件引用 -->
<script src="$!{basePath}/plug-in-ui/js/jquery-1.9.1.js"></script>
<!-- bootstrap组件引用 -->
<link href="$!{basePath}/plug-in-ui/css/bootstrap.min.css" rel="stylesheet">
<script src="$!{basePath}/plug-in-ui/hplus/js/bootstrap.min.js"></script>

<!-- bootstrap table组件以及中文包的引用-->
<link href="$!{basePath}/plug-in-ui/css/bootstrap-user-defined/bootstrap-table.min.css" rel="stylesheet">
<script src="$!{basePath}/plug-in-ui/hplus/js/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script src="$!{basePath}/plug-in-ui/hplus/js/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>

<!-- Layer组件引用 -->
<script src="$!{basePath}/plug-in-ui/layer/layer.js"></script>
<script src="$!{basePath}/plug-in-ui/layer/laydate/laydate.js"></script>

<!-- 通用组件引用--> 
<link href="$!{basePath}/plug-in-ui/css/bootstrap-user-defined/default.css" rel="stylesheet" />
<script src="$!{basePath}/plug-in-ui/js/bootstrap-user-defined/bootstrap-curdtools.js"></script>
<#assign orderByCreateDate = false />
<#list columnDatas as po>
	<#if po.columnName=='createDate'>
		<#assign orderByCreateDate = true />
		<#break>
	</#if>
</#list>
</head>
<body>

	<div class="panel-body" style="padding-bottom: 0px;">
		<!-- 搜索 -->
		<div class="accordion-group">
			<div id="collapse_search" class="accordion-body collapse">
				<div class="accordion-inner">
					<div class="panel panel-default" style="margin-bottom: 0px;">
						<div class="panel-body">
							<form id="formSubmit" onkeydown="if(event.keyCode==13){doSearch();return false;}">
								<#list columnDatas as po>
										<#if po.columnType?index_of("datetime")!=-1>
											<#if po.columnType =='group'>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}_begin">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm laydate-datetime"id="${po.domainPropertyName}_begin" name="${po.domainPropertyName}_begin"/> 
													<span class="input-group-addon" >
								                        <span class="glyphicon glyphicon-calendar"></span>
								                    </span>
													<span class="input-group-addon input-sm">~</span> 
													<input type="text" class="form-control input-sm laydate-datetime" id="${po.domainPropertyName}_end"name="${po.domainPropertyName}_end" />
													<span class="input-group-addon" >
								                        <span class="glyphicon glyphicon-calendar"></span>
								                    </span>
												</div>
											</div>
											<#else>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm laydate-datetime"id="${po.domainPropertyName}" name="${po.domainPropertyName}" />
													<span class="input-group-addon" >
							                        	<span class="glyphicon glyphicon-calendar"></span>
								                    </span>
												</div>
											</div>
											</#if>
										<#elseif po.columnType?index_of("date")!=-1>
											<#if po.columnType =='group'>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}_begin">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm laydate-date" id="${po.domainPropertyName}_begin" name="${po.domainPropertyName}_begin"/> 
													<span class="input-group-addon" >
								                        <span class="glyphicon glyphicon-calendar"></span>
								                    </span>
													<span class="input-group-addon input-sm">~</span> 
													<input type="text" class="form-control input-sm laydate-date" id="${po.domainPropertyName}_end" name="${po.domainPropertyName}_end"/>
													<span class="input-group-addon" >
								                        <span class="glyphicon glyphicon-calendar"></span>
								                    </span>
												</div>
											</div>
											<#else>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm laydate-date"id="${po.domainPropertyName}" name="${po.domainPropertyName}" />
													<span class="input-group-addon" >
							                        	<span class="glyphicon glyphicon-calendar"></span>
								                    </span>
												</div>
											</div>
											</#if>
										<#elseif po.columnType=='checkbox'|| po.columnType=='radio'>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}">${po.columnComment}：</label>
												<div class=" ${po.domainPropertyName}-search input-group" style="width: 100%"></div>
											</div>
										<#elseif  po.columnType=='select' || po.columnType=='list'>
											<div class="col-xs-12 col-sm-6 col-md-4">
									    		<label for="${po.domainPropertyName}">${po.columnComment}：</label>
									    		<div class="input-group" style="width:100%">
										    		<select class="form-control input-sm" id="${po.domainPropertyName}" name="${po.domainPropertyName}"></select>
									    		</div>
								    		</div>
										<#else>
											<#if po.columnType =='group'>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}_begin">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm" id="${po.domainPropertyName}_begin" name="${po.domainPropertyName}_begin"/> 
													<span class="input-group-addon input-sm">~</span> 
													<input type="text" class="form-control input-sm" id="${po.domainPropertyName}_end" name="${po.domainPropertyName}_end"/>
												</div>
											</div>
											<#else>
											<div class="col-xs-12 col-sm-6 col-md-4">
												<label for="${po.domainPropertyName}">${po.columnComment}：</label>
												<div class="input-group" style="width: 100%">
													<input type="text" class="form-control input-sm" id="${po.domainPropertyName}" name="${po.domainPropertyName}"/>
												</div>
											</div>
											</#if>
										</#if>
								</#list>
								
								<div class="col-xs-12 col-sm-6 col-md-4">
									<div class="input-group col-md-12" style="margin-top: 20px">
										<a type="button" onclick="searchList();" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><span class="glyphicon glyphicon-search" aria-hidden="true"></span>查询</a> 
										<a type="button" onclick="searchRest();" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span>重置</a>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="panel-body" style="padding-top: 0px; padding-bottom: 0px;">
		<!-- toolbar -->
		<div id="toolbar">
			<button onclick="add('录入','$!{basePath}/${projectName}/${lowerName}.do?toAdd','${lowerName}List',1000,600)" id="btn_add" class="btn btn-primary btn-sm">
				<span class="glyphicon glyphicon-plus" aria-hidden="true"></span> 录入
			</button>
			<button onclick="update('编辑','$!{basePath}/${projectName}/${lowerName}.do?toEdit','${lowerName}List',1000,600)" id="btn_edit" class="btn btn-success btn-sm">
				<span class="glyphicon glyphicon-pencil" aria-hidden="true"></span> 编辑
			</button>
			<button onclick="deleteALLSelect('批量删除','$!{basePath}/${projectName}/${lowerName}.do?doBatchDel','${lowerName}List',600,400)" id="btn_delete" class="btn btn-danger btn-sm">
				<span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 批量删除
			</button>
			<button onclick="update('查看','$!{basePath}/${projectName}/${lowerName}.do?toEdit&load=detail','${lowerName}List',600,400)" class="btn btn-info btn-sm">
				<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 查看
			</button>
			<a class="btn btn-default btn-sm" data-toggle="collapse" href="#collapse_search" id="btn_collapse_search"> 
				<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 检索 
			</a>
		</div>
		<!-- data table -->
		<div class="table-responsive">
			<!-- class="text-nowrap" 强制不换行 -->
			<table id="${lowerName}List"></table>
		</div>
	</div>
	<script type="text/javascript">
		<#-- update-begin-author:jiaqiankun date:20180704 for:TASK #2882 【bootstrapTable】代码生成器存在的问题 -->
		$(".laydate-datetime").each(function(){
			var _this = this;
			laydate.render({
			  elem: this,
			  format: 'yyyy-MM-dd HH:mm:ss',
			  type: 'datetime'
			});
		});
		$(".laydate-date").each(function(){
			var _this = this;
			laydate.render({
			  elem: this
			});
		});
		<#-- update-end-author:jiaqiankun date:20180704 for:TASK #2882 【bootstrapTable】代码生成器存在的问题 -->
		var ${lowerName}ListdictsData = {};
		$(function() {
			var promiseArr = [];
			<#assign optionCodes="">
			<#list columnDatas as po>
			<#if po.columnType=='checkbox' || po.columnType=='radio' || po.columnType=='select' || po.columnType=='list'>
			<#if optionCodes?index_of(po.dictField) lt 0>
			<#assign optionCodes=optionCodes+","+po.dictField >
			promiseArr.push(new Promise(function(resolve, reject) {
				initDictByCode(${lowerName}ListdictsData,"${po.dictField}",resolve);
			}));
			</#if>
			</#if>
			</#list>
			
			Promise.all(promiseArr).then(function(results) {
				<#list columnDatas as po>
				<#if po.columnType=='checkbox'||po.columnType=='radio'>
				loadSearchFormDicts($("#formSubmit").find(".${po.domainPropertyName}-search"),${lowerName}ListdictsData.${po.dictField},"${po.columnType}","${po.domainPropertyName}");
				<#elseif  po.columnType=='select' || po.columnType=='list'>
				loadSearchFormDicts($("#formSubmit").find("select[name='${po.domainPropertyName}']"),${lowerName}ListdictsData.${po.dictField},"select");
				</#if>
				</#list>
			    
			}).catch(function(err) {
			 	console.log('Catch: ', err);
			});
			
			//1.初始化Table
			var oTable = new TableInit();
			oTable.Init();

			//判断是否选中表格中的数据，选中后可编辑或删除
			$('#${lowerName}List').on(
					'check.bs.table uncheck.bs.table load-success.bs.table '
							+ 'check-all.bs.table uncheck-all.bs.table',
					function() {
						$('#btn_delete').prop('disabled',!$('#${lowerName}List').bootstrapTable('getSelections').length);
						$('#btn_edit').prop('disabled',$('#${lowerName}List').bootstrapTable('getSelections').length != 1);
					});
			});

		var TableInit = function() {
			var oTableInit = new Object();
			//初始化Table
			oTableInit.Init = function() {
				$('#${lowerName}List').bootstrapTable({
									url : '$!{basePath}/${projectName}/${lowerName}.do?datagrid', //请求后台的URL（*）
									method : 'get', //请求方式（*）
									toolbar : '#toolbar', //工具按钮用哪个容器
									striped : true, //是否显示行间隔色
									cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
									pagination : true, //是否显示分页（*）
									queryParams : oTableInit.queryParams,//传递参数（*）
									sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
									pageNumber : 1, //初始化加载第一页，默认第一页
									pageSize : 10, //每页的记录行数（*）
									pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
									strictSearch : true,
									showColumns : true, //是否显示所有的列
									showRefresh : true, //是否显示刷新按钮
									minimumCountColumns : 2, //最少允许的列数
									clickToSelect : true, //是否启用点击选中行
									height : $(window).height() - 35, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
									uniqueId : "id", //每一行的唯一标识，一般为主键列
									showToggle : true, //是否显示详细视图和列表视图的切换按钮
									cardView : false, //是否显示详细视图
									detailView : false, //是否显示父子表
									showExport : true, //显示到处按钮
									<#if orderByCreateDate == true>
									sortName:'createDate',
									<#else>
									sortName:'id',
									</#if>
									sortOrder:'desc',
									columns : [
											// 复选框
											{
												checkbox : true,
												align : 'center'
											},
											{
												title : '序号',
												width : 5,
												align : 'center',
												switchable : false,
												formatter : function(value,row, index) {
													//return index+1; //序号正序排序从1开始
													var pageSize = $('#${lowerName}List')
															.bootstrapTable('getOptions').pageSize;
													var pageNumber = $('#${lowerName}List').bootstrapTable('getOptions').pageNumber;
													return pageSize* (pageNumber - 1) + index + 1;
												}
											},
											<#list columnDatas as po>
											<#if po.domainPropertyName != 'id'>
											{
												field : '${po.domainPropertyName}',
												title : '${po.columnComment}',
												valign : 'middle',
												width : '120',
												<#if po.isShowList?if_exists?html =='N'>
												visible:false,
												</#if>
												align : 'center',
												switchable : true,
												<#if po.columnType?index_of("datetime")!=-1>
												formatter : function(value, rec, index) {
													 return getSmpFormatDateByLong(value, true); 
												}
												<#elseif po.columnType?index_of("date")!=-1>
												formatter : function(value, rec, index) {
													 return getSmpFormatDateByLong(value, false); 
												}
												</#if>
											},
											</#if>
											</#list>
											{
												title : "操作",
												align : 'center',
												valign : 'middle',
												width : 100,
												formatter : function(value,row, index) {
													if (!row.id) {
														return '';
													}
													var href = '';
													href += "<a href='javascript:void(0);'  class='ace_button'  onclick=delObj('$!{basePath}/${projectName}/${lowerName}.do?doDelete&id="
															+ row.id
															+ "','testRulesList')>  <i class='fa fa-trash-o' aria-hidden='true'></i> ";
													href += "删除</a>&nbsp;";
													return href;
												}
											} ],
									onLoadSuccess : function() { //加载成功时执行
										console.info("加载成功");
									},
									onLoadError : function() { //加载失败时执行
										console.info("加载数据失败");
									}
								});
			};

			//得到查询的参数
			oTableInit.queryParams = function(params) {
				var temp = { //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
					pageSize : params.limit, // 每页要显示的数据条数
					offset : params.offset, // 每页显示数据的开始行号
					sort : params.sort, // 排序规则
					order : params.order,
					rows : params.limit, //页面大小
					page : (params.offset / params.limit) + 1, //页码
					pageIndex : params.pageNumber,//请求第几页
					field : '<#list columnDatas as po>${po.domainPropertyName},</#list>'
				};

				var params = $("#formSubmit").serializeArray();
				for (x in params) {
					temp[params[x].name] = params[x].value;
				}
				return temp;
			};
			return oTableInit;
		};
		
		function searchList() {
			reloadTable();
		}

		function reloadTable() {
			$('#${lowerName}List').bootstrapTable('refresh');
		}

		function searchRest() {
			$('#formSubmit').find(':input').each(function() {
		    	if("checkbox"== $(this).attr("type")){
		    		$("input:checkbox[name='" + $(this).attr('name') + "']").attr('checked',false);
				}else if("radio"== $(this).attr("type")){
					$("input:radio[name='" + $(this).attr('name') + "']").attr('checked',false);
				}else{
					$(this).val("");
				}
		    });
		    $('#formSubmit').find("input[type='checkbox']").each(function() {
		        $(this).attr('checked', false);
		    });
		    $('#formSubmit').find("input[type='radio']").each(function() {
		        $(this).attr('checked', false);
		    });
			reloadTable();
		}
		//高级查询模态框
		function bootstrapQueryBuilder() {
			$('#superQueryModal').modal({
				backdrop : false
			});
		}
	</script>

	<#if (cgformConfig.listJs.cgJsStr)?? && cgformConfig.listJs.cgJsStr!="">
    <script type="text/javascript">
	 //JS增强
	 ${cgformConfig.listJs.cgJsStr?if_exists}
	</script>
	</#if>
	
	<script type="text/javascript">
	//扩展Date的format方法   
    Date.prototype.format = function (format) {  
        var o = {  
            "M+": this.getMonth() + 1,  
            "d+": this.getDate(),  
            "h+": this.getHours(),  
            "m+": this.getMinutes(),  
            "s+": this.getSeconds(),  
            "q+": Math.floor((this.getMonth() + 3) / 3),  
            "S": this.getMilliseconds()  
        }  
        if (/(y+)/.test(format)) {  
            format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
        }  
        for (var k in o) {  
            if (new RegExp("(" + k + ")").test(format)) {  
                format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));  
            }  
        }  
        return format;  
    }  
    /**   
    *转换日期对象为日期字符串   
    * @param date 日期对象   
    * @param isFull 是否为完整的日期数据,   
    *               为true时, 格式如"2000-03-05 01:05:04"   
    *               为false时, 格式如 "2000-03-05"   
    * @return 符合要求的日期字符串   
    */    
    function getSmpFormatDate(date, isFull) {  
        var pattern = "";  
        if (isFull == true || isFull == undefined) {  
            pattern = "yyyy-MM-dd hh:mm:ss";  
        } else {  
            pattern = "yyyy-MM-dd";  
        }  
        return getFormatDate(date, pattern);  
    }  
    /**   
    *转换当前日期对象为日期字符串   
    * @param date 日期对象   
    * @param isFull 是否为完整的日期数据,   
    *               为true时, 格式如"2000-03-05 01:05:04"   
    *               为false时, 格式如 "2000-03-05"   
    * @return 符合要求的日期字符串   
    */    
  
    function getSmpFormatNowDate(isFull) {  
        return getSmpFormatDate(new Date(), isFull);  
    }  
    /**   
    *转换long值为日期字符串   
    * @param l long值   
    * @param isFull 是否为完整的日期数据,   
    *               为true时, 格式如"2000-03-05 01:05:04"   
    *               为false时, 格式如 "2000-03-05"   
    * @return 符合要求的日期字符串   
    */    
  
    function getSmpFormatDateByLong(l, isFull) {  
        return getSmpFormatDate(new Date(l), isFull);  
    }  
    /**   
    *转换long值为日期字符串   
    * @param l long值   
    * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss   
    * @return 符合要求的日期字符串   
    */    
  
    function getFormatDateByLong(l, pattern) {  
        return getFormatDate(new Date(l), pattern);  
    }  
    /**   
    *转换日期对象为日期字符串   
    * @param l long值   
    * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss   
    * @return 符合要求的日期字符串   
    */    
    function getFormatDate(date, pattern) {  
        if (date == undefined) {  
            date = new Date();  
        }  
        if (pattern == undefined) {  
            pattern = "yyyy-MM-dd hh:mm:ss";  
        }  
        return date.format(pattern);  
    } 
    </script> 
</body>
</html>