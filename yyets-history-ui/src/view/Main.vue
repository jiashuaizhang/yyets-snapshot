<template>
  <div style="border: 1px solid #eee">
    <div class="grid-content bg-purple-light div-content">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline" @submit.native.prevent>
        <el-form-item label="剧目:" class="form-item">
          <el-input v-model="searchForm.name" placeholder="剧目"></el-input>
        </el-form-item>
        <el-form-item class="form-item">
          <el-button type="primary" :loading="loading" @click="onSubmit">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-table
        :data="tableData"
        :height="tableHeight"
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
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="750px">
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item label="资源分组:" class="form-item">
          <el-select placeholder="资源分组" v-model="linkGroup" :change="linkGroupChange(linkGroup)">
            <el-option
              v-for="(item,i) in resourceLinks"
              :key="`${resourceLinks.name}${item.name}${i}`"
              :label="item.name"
              :value="i"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型:" class="form-item">
          <el-select placeholder="操作类型" v-model="operationType">
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
      </el-form>
      <el-table :data="resourceLinksTableData" height="300px">
        <el-table-column property="episode" label="分集" width="150"></el-table-column>
        <el-table-column property="name" label="文件名" width="200"></el-table-column>
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
     <textarea id="hiddenLink" style="display: none"></textarea>
  </div>
</template>

<script>
  const baseUri = '/resource/page/';
  const pageNo = 1;
  export default {
    data() {
      return {
        searchForm: {
          name: '',
        },
        loading: false,
        dialogVisible: false,
        resourceLinks: [],
        resourceLinksTableData: [],
        resourceRow: {},
        tableData: [],
        dialogTitle: '资源列表',
        linkGroup: 0,
        operationType: 0,
        tableHeight: window.screen.height - 290
      }
    },
    methods: {
      viewSeason(resource, i) {
        this.resourceRow = resource;
        let season = resource.seasons[i];
        this.dialogTitle = `${resource.name} ${season.name}`;
        this.resourceLinks = season.groups;
        let defaultIndex = this.resourceLinks.findIndex(link => link.name === 'HR-HDTV'
          || link.name === 'MP4');
        this.linkGroup = defaultIndex > -1 ? defaultIndex : 0;
        this.resourceLinksTableData = this.resourceLinks[this.linkGroup].items;
        this.dialogVisible = true;
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
          hiddenLink.style.display = 'none';
          hiddenLink.id = textareaId;
        }
        hiddenLink.value = link;
        hiddenLink.select();
        document.execCommand("copy");
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
          });
        }
      },
      onSubmit() {
        if (!this.searchForm.name) {
          this.$message({
            message: '请输入剧目',
            type: 'warning'
          });
          return;
        }
        let uri = baseUri + pageNo + '/' + this.searchForm.name;
        this.loading = true;
        this.$axios.get(uri).then(response => {
          this.loading = false;
          this.tableData = response.data;
          this.$message({
            message: '查询成功',
            type: 'success'
          });
        }).catch(error => {
          this.loading = false;
          console.dir(error);
          this.$message.error('查询失败');
        });
      }
    },
    created() {
      document.onkeydown = e => {
        let keyCode = e.code;
        if (keyCode === 'Enter') {
          this.onSubmit();
        }
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
</style>
