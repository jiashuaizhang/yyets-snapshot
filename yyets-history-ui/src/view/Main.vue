<template>
  <div style="border: 1px solid #eee">
    <div class="grid-content bg-purple-light div-content">
      <el-form :inline="true" :model="searchForm" @submit.native.prevent>
        <el-form-item label="剧目:" class="form-item">
          <el-input v-model="searchForm.name" placeholder="剧目" @keyup.enter.native="search(true)"></el-input>
        </el-form-item>
        <el-form-item class="form-item">
          <el-button type="primary" :loading="loading" @click="search(true)">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-table
        :data="tableData"
        height="75vh"
        border
        style="width: 100%">
        <el-table-column
          prop="name"
          label="剧目"
          width="180"
          fixed="left">
        </el-table-column>
        <el-table-column
          prop="nameEN"
          label="英文名"
          width="180"
          fixed="left">
        </el-table-column>
        <el-table-column
          prop="channel"
          label="频道"
          width="180">
        </el-table-column>
        <el-table-column
          prop="area"
          label="地区">
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="350">
          <template slot-scope="scope">
            <el-button type="text" size="small" style="margin-left: 10px"
                       v-for="(item, i) in scope.row.seasons"
                       @click="viewSeason(scope.row, i)" :key="`${scope.row.id}${i}`">{{ item.name }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="1000px" top="8vh">
      <el-form :inline="true">
        <el-form-item label="资源分组:" class="form-item">
          <el-select placeholder="资源分组" v-model="linkGroup" @change="linkGroupChange(linkGroup)">
            <el-option
              v-for="(item,i) in resourceLinks"
              :key="`${resourceLinks.name}${item.name}${i}`"
              :label="item.name"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型:" class="form-item">
          <el-select placeholder="操作类型" v-model="operationType" @change="operationTypeChange()">
            <el-option
              label="复制"
              :value="0"
            />
            <el-option
              label="查看"
              :value="1"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="下载渠道:" class="form-item" v-show="operationType === 0">
          <el-select v-model="downloadWay" placeholder="" style="width: 100px">
            <el-option
              v-for="(item,i) in downloadWays"
              :key="`${resourceLinks.name}${item}${i}`"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item class="form-item" style="position: absolute;right: 50px">
          <el-button type="success" icon="el-icon-document-copy" circle class="form-item"
                     title="批量复制" @click="batchCopy()" :disabled="operationType === 1"/>
        </el-form-item>
      </el-form>
      <el-table :data="resourceLinksTableData" height="330px"
                ref="multipleTable" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" v-if="operationType === 0"></el-table-column>
        <el-table-column property="episode" label="分集" width="55"></el-table-column>
        <el-table-column property="name" label="文件名" width="575"></el-table-column>
        <el-table-column property="size" label="大小"></el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
        >
          <template slot-scope="scope">
            <el-button type="text" size="small" style="margin-left: 10px"
                       v-for="(item, i) in scope.row.links"
                       @click="dealLink(scope.row, i)"
                       :key="`${resourceRow.id}${scope.row.episode}${i}`">{{ item.way }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <el-pagination class="pagination"
                   @size-change="handleSizeChange"
                   @current-change="handleCurrentChange"
                   :current-page="searchForm.pageNo"
                   :page-sizes="[10, 20, 30, 40, 50]"
                   :page-size="searchForm.pageSize"
                   layout="total, sizes, prev, pager, next, jumper"
                   background
                   :total="total"
                   v-show="tableData.length > 0">
    </el-pagination>
     <textarea id="hiddenLink" class="hidden-link"></textarea>
  </div>
</template>

<script>
  const baseUri = '/resource/page';
  export default {
    data() {
      return {
        searchForm: {
          name: '',
          pageNo: 1,
          pageSize: 10
        },
        total: 0,
        loading: false,
        dialogVisible: false,
        resourceLinks: [],
        resourceLinksTableData: [],
        resourceRow: {},
        tableData: [],
        dialogTitle: '资源列表',
        linkGroup: 0,
        operationType: 0,
        multipleSelection: [],
        downloadWays: [],
        downloadWay: ''
      }
    },
    methods: {
      batchCopy() {
        if (this.multipleSelection.length === 0) {
          this.$message({
            message: '请选择数据',
            type: 'warning'
          });
          return;
        }
        let text = '';
        for (let i = 0; i < this.multipleSelection.length; i++) {
          let links = this.multipleSelection[i].links;
          if (!links || links.length === 0) {
            continue;
          }
          let link = links.filter(link => link.way === this.downloadWay).map(link => link.address).pop();
          let trim;
          if (link && (trim = link.trim()).length > 0) {
            text += trim;
            if (i !== this.multipleSelection.length - 1) {
              text += '\n';
            }
          }
        }
        if (text) {
          this.copyLink(text);
        }
      },
      handleSelectionChange(rows) {
        this.multipleSelection = rows;
      },
      handleSizeChange(pageSize) {
        this.searchForm.pageSize = pageSize;
        this.searchForm.pageNo = 1;
        this.search();
      },
      operationTypeChange() {
        if (this.$refs.multipleTable)
          this.$refs.multipleTable.clearSelection();
      },
      handleCurrentChange(pageNo) {
        this.searchForm.pageNo = pageNo;
        this.search();
      },
      viewSeason(resource, i) {
        this.resourceRow = resource;
        let season = resource.seasons[i];
        this.dialogTitle = `${resource.name} ${season.name}`;
        this.resourceLinks = season.groups;
        let defaultIndex = this.resourceLinks.findIndex(link => link.name === 'HR-HDTV'
          || link.name === 'MP4');
        this.linkGroup = defaultIndex > -1 ? defaultIndex : 0;
        this.resourceLinksTableData = this.resourceLinks[this.linkGroup].items;
        this.resetDownloadWays();
        this.dialogVisible = true;
      },
      resetDownloadWays() {
        if (!this.resourceLinksTableData || this.resourceLinksTableData.length === 0) {
          return;
        }
        this.downloadWays = [];
        this.downloadWay = '';
        let data = this.resourceLinksTableData[0];
        let links = data.links;
        if (links && links.length > 0) {
          let ways = links.map(link => link.way);
          this.downloadWays = ways;
          this.downloadWay = ways[0];
        }
      },
      linkGroupChange(linkGroup) {
        if (!this.resourceRow.seasons || this.resourceRow.seasons.length === 0) {
          return;
        }
        this.resourceLinksTableData = this.resourceLinks[linkGroup].items;
      },
      copyLink(link) {
        let textareaId = 'hiddenLink';
        let hiddenLink = document.getElementById(textareaId);
        if (!hiddenLink) {
          hiddenLink = document.createElement('textarea');
          hiddenLink.class = 'hidden-link';
          hiddenLink.id = textareaId;
        }
        hiddenLink.innerHTML = link;
        hiddenLink.select();
        document.execCommand('copy');
        this.$message({
          message: '复制成功',
          type: 'success'
        });
      },
      dealLink(row, i) {
        let link = row.links[i].address;
        console.log(link);
        if (this.operationType === 0) {
          this.copyLink(link);
        } else {
          this.$alert(`<textarea style="width:380px;height:60px;">${link}</textarea>`, '查看链接', {
            dangerouslyUseHTMLString: true
          }).catch(e => {
          });
        }
      },
      search(newIn) {
        if (newIn) {
          this.searchForm.pageNo = 1;
        }
        if (this.searchForm.name) {
          this.searchForm.name = this.searchForm.name.trim();
        }
        let queryParams = '';
        let i = 0;
        for (let key in this.searchForm) {
          let prefix = i === 0 ? '?' : '&';
          queryParams += `${prefix}${key}=${this.searchForm[key]}`;
          i++;
        }
        let uri = baseUri + queryParams;
        this.loading = true;
        this.$axios.get(uri).then(response => {
          this.loading = false;
          let data = response.data;
          this.tableData = data.list;
          this.total = data.total;
          this.searchForm.pageSize = data.pageSize;
          this.searchForm.pageNo = data.pageNum;
          if (newIn) {
            this.$message({
              message: '查询成功',
              type: 'success'
            });
          }
        }).catch(error => {
          this.loading = false;
          console.dir(error);
          this.$message.error('查询失败');
        });
      }
    }
  }
</script>
<style>
  .div-content {
    margin-top: 20px;
    width: 100%;
  }

  .form-item {
    margin-left: 20px;
    float: left;
  }

  .pagination {
    text-align: left;
    margin-left: 5px;
  }

  .hidden-link {
    position: fixed;
    bottom: -100px;
  }
</style>
