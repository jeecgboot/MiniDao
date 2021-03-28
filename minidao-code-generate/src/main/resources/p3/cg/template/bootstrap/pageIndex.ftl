<!DOCTYPE html>
<html lang="en">
<head>
<title>Bootstrap</title>
<meta name="viewport" content="width=device-width" />
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
</head>
<body>
<div class="panel-body" style="padding-bottom:0px;">
        <!-- 搜索 -->
		<div class="accordion-group">
			<div id="collapse_search" class="accordion-body collapse">
				<div class="accordion-inner">
					<div class="panel panel-default" style="margin-bottom: 0px;">
            				<div class="panel-body" >
			                <form id="searchForm" class="form form-horizontal" action="$!{basePath}/${projectName}/${lowerName}.do?list" method="post">
			                   	<#list columnDatas as item>
									<#if item.columnName?lower_case != 'id'>
									<#if item.columnName?lower_case != 'del_stat' 
										    && item.columnName?lower_case != 'create_by'
											&& item.columnName?lower_case != 'create_name' 
											&& item.columnName?lower_case != 'create_date' 
											&& item.columnName?lower_case != 'update_by'
											&& item.columnName?lower_case != 'update_name' 
											&& item.columnName?lower_case != 'update_date' 
											&& item.columnName?lower_case != 'creator'
											&& item.columnName?lower_case != 'editor' 
											&& item.columnName?lower_case != 'create_dt' 
											&& item.columnName?lower_case != 'edit_dt' 
											&& item.columnName?lower_case != 'last_edit_dt' 
											&& item.columnName?lower_case != 'record_version'>
									<#if item.columnType == "datetime" ||item.columnType == "date" || item.columnType == "timestamp">
										 <div class="col-xs-12 col-sm-6 col-md-4">
					                         <label for="${item.domainPropertyName}">${item.columnComment}</label>
					                         <div class="input-group col-md-12">
						                         <input type="text" class="form-control input-sm" name="${item.domainPropertyName}" id="${item.domainPropertyName}" />
							                     <span class="input-group-addon" >
							                        <span class="glyphicon glyphicon-calendar"></span>
							                     </span>
				                               </div>
			                             </div>
									<#else>
										 <div class="col-xs-12 col-sm-6 col-md-4">
											<label for="${item.domainPropertyName}">${item.columnComment}</label>
											<div class="input-group col-md-12">
												<input type="text" name="${item.domainPropertyName}" id="${item.domainPropertyName}" value="$!{${lowerName}.${item.domainPropertyName}}" class="form-control input-sm">
											</div>
										 </div>
									</#if>
									</#if>
									</#if>
								</#list>
			                    <div class="col-xs-12 col-sm-6 col-md-4">
			                         <div  class="input-group col-md-12" style="margin-top:20px">
			                         <a type="button" onclick="${lowerName}Search();" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> 查询</a>
			                         <a type="button" onclick="${lowerName}Rest();" class="btn btn-primary btn-rounded  btn-bordered btn-sm"><span class="glyphicon glyphicon-repeat" aria-hidden="true"></span> 重置</a>
			                         </div>
			                    </div>
			                </form>
			                </div>
			             </div>
			       </div>
			</div>
		</div>
        <div id="toolbar">
            <button id="btn_add" type="button" class="btn btn-primary btn-sm" onclick="add('新增','$!{basePath}/${projectName}/${lowerName}.do?toAdd','${lowerName}List',600,400)">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
            </button>
            <button id="btn_edit" type="button" class="btn btn-success btn-sm" onclick="update('修改','$!{basePath}/${projectName}/${lowerName}.do?toEdit','${lowerName}List',600,400)">
                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
            </button>
            <button id="btn_delete" type="button" class="btn btn-danger btn-sm"  onclick="deleteALLSelect('批量删除','$!{basePath}/${projectName}/${lowerName}.do?batchDelete','${lowerName}List',600,400)">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>批量删除
            </button>
            <a class="btn btn-default btn-sm" data-toggle="collapse" href="#collapse_search" id="btn_collapse_search" >
						<span class="glyphicon glyphicon-search" aria-hidden="true"></span> 检索 </a>
        </div>
        <div class="table-responsive">
            <!-- class="text-nowrap" 强制不换行 -->
         	<table id="${lowerName}List"></table>
        </div>
    </div>
</body>
</html>
<script>
	$(function () {
	    //1.初始化Table
	    var oTable = new TableInit();
	    oTable.Init();
	    
	    //判断是否选中表格中的数据，选中后可编辑或删除
	    $('#${lowerName}List').on('check.bs.table uncheck.bs.table load-success.bs.table ' +
	            'check-all.bs.table uncheck-all.bs.table', function () {
	        $('#btn_delete').prop('disabled', ! $('#${lowerName}List').bootstrapTable('getSelections').length);
	        $('#btn_edit').prop('disabled', $('#${lowerName}List').bootstrapTable('getSelections').length!=1);
	    });
	    <#list columnDatas as item>
		<#if item.columnType == "datetime" ||item.columnType == "date" || item.columnType == "timestamp">
		//日期控件初始化
	    laydate.render({
		   elem: '#${item.domainPropertyName}'
		  ,type: '${item.columnType}'
		  ,trigger: 'click' //采用click弹出
		  ,ready: function(date){
		  	 $("#${item.domainPropertyName}").val(DateJsonFormat(date,this.format));
		  }
		});
		</#if>
		</#list>
	});
	
	
	var TableInit = function () {
	    var oTableInit = new Object();
	    var columnDatas=columnDatas;
	    //初始化Table
	    oTableInit.Init = function () {
	        $('#${lowerName}List').bootstrapTable({
	            url: '$!{basePath}/${projectName}/${lowerName}.do?datagrid',         //请求后台的URL（*）
	            method: 'get',                      //请求方式（*）
	            toolbar: '#toolbar',                //工具按钮用哪个容器
	            striped: true,                      //是否显示行间隔色
	            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
	            pagination: true,                   //是否显示分页（*）
	            sortable: true,                     //是否启用排序
	            sortOrder: "asc",                   //排序方式
	            queryParams: oTableInit.queryParams,//传递参数（*）
	            sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
	            pageNumber:1,                       //初始化加载第一页，默认第一页
	            pageSize: 10,                       //每页的记录行数（*）
	            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
	            strictSearch: true,
	            showColumns: true,                  //是否显示所有的列
	            showRefresh: true,                  //是否显示刷新按钮
	            minimumCountColumns: 2,             //最少允许的列数
	            clickToSelect: true,                //是否启用点击选中行
	            height : $(window).height()-35,   //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
	            uniqueId: "id",                   //每一行的唯一标识，一般为主键列
	            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
	            cardView: false,                    //是否显示详细视图
	            detailView: false,                   //是否显示父子表
	  	        showExport: true,                    //显示到处按钮
	            columns: 
	            [{
	                checkbox: true, // 显示一个勾选框
	                align: 'center' // 居中显示,
	                
	            },{
	                title: '序号',
	                width:5 ,
	                align:'center',
	                switchable:false,
	                formatter:function(value,row,index){
	                    //return index+1; //序号正序排序从1开始
	                    var pageSize=$('#${lowerName}List').bootstrapTable('getOptions').pageSize;
	                    var pageNumber=$('#${lowerName}List').bootstrapTable('getOptions').pageNumber;
	                    return pageSize * (pageNumber - 1) + index + 1; 
	                }
	                },
	               <#list columnDatas as item>
	               <#if item.columnName?lower_case != 'id'>
	               <#if item.columnName?lower_case != 'del_stat' 
										    && item.columnName?lower_case != 'create_by'
											&& item.columnName?lower_case != 'create_name' 
											&& item.columnName?lower_case != 'create_date' 
											&& item.columnName?lower_case != 'update_by'
											&& item.columnName?lower_case != 'update_name' 
											&& item.columnName?lower_case != 'update_date' 
											&& item.columnName?lower_case != 'creator'
											&& item.columnName?lower_case != 'editor' 
											&& item.columnName?lower_case != 'create_dt' 
											&& item.columnName?lower_case != 'edit_dt' 
											&& item.columnName?lower_case != 'last_edit_dt' 
											&& item.columnName?lower_case != 'record_version'>
	               <#if item.columnType == "datetime" ||item.columnType == "date" || item.columnType == "timestamp">
				   {
	                field: '${item.domainPropertyName}',
	                title: '${item.columnComment}',
	                align: 'center',
	                valign: 'middle',
	                formatter:function(value,row,index){
	                    return getSmpFormatDateByLong(value,false); 
	                }
	                }, 
	                <#else>
	                 {
	                field: '${item.domainPropertyName}',
	                title: '${item.columnComment}',
	                align: 'center',
	                valign: 'middle',
	                sortable:true
	                }, 
					</#if>
					</#if>
					</#if>
				  </#list>
	             {
	                title: "操作",
	                align: 'center',
	                valign: 'middle',
	                width: 160, // 定义列的宽度，单位为像素px
	                formatter: function (value, row, index) {
	                    return '<button class="btn btn-danger btn-xs" onclick="delObj(\'$!{basePath}/${projectName}/${lowerName}.do?doDelete&id='+row.id+'\',\'${lowerName}List\')"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</button>';
	                }
	            }],
	            onLoadSuccess: function(){  //加载成功时执行
	                console.info("加载成功");
	          },
	          onLoadError: function(){  //加载失败时执行
	                console.info("加载数据失败");
	          }
	        });
	    };
	
	    //得到查询的参数
	    oTableInit.queryParams = function (params) {
	        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
	            pageSize: params.limit, // 每页要显示的数据条数
	            offset: params.offset,  //页码
	            sort: params.sort, // 排序规则
	            order: params.order,
	            rows: params.limit,                         //页面大小
	            page: (params.offset / params.limit) + 1,   //页码
	            pageIndex:params.pageNumber,//请求第几页
	        };
	        var params = $("#searchForm").serializeArray();  
	        for( x in params ){  
	            temp[params[x].name] = params[x].value;  
	        }  
	        return temp;
	    };
	    return oTableInit;
	};
	
	function ${lowerName}Search(){
		reloadTable();
	}
	
	function reloadTable(){
		$('#${lowerName}List').bootstrapTable('refresh');
	}
	
	function ${lowerName}Rest(){
		$("#searchForm  input").val("");
		$("#searchForm  select").val("");
		$("#searchForm  .select-item").html("");
		reloadTable();
	}
	
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