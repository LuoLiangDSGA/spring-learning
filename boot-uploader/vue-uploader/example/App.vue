<template>
  <uploader :options="options" :file-status-text="statusText" class="uploader-example" ref="uploader"
            @file-complete="fileComplete" @complete="complete"></uploader>
</template>

<script>
  import axios from 'axios'
  import qs from 'qs'

  export default {
    data() {
      return {
        options: {
          target: '/boot/uploader/chunk',
          testChunks: true,
          simultaneousUploads: 1,
          chunkSize: 10 * 1024 * 1024,
          // checkChunkUploadedByResponse: function (chunk, message) {
          //   let objMessage = {}
          //   try {
          //     objMessage = JSON.parse(message)
          //   } catch (e) {
          //   }
          //   // fake response
          //   // objMessage.uploaded_chunks = [2, 3, 4, 5, 6, 8, 10, 11, 12, 13, 17, 20, 21]
          //   // check the chunk is uploaded
          //   return (objMessage.uploaded_chunks || []).indexOf(chunk.offset + 1) >= 0
          // }
        },
        attrs: {
          accept: 'image/*'
        },
        statusText: {
          success: '成功了',
          error: '出错了',
          uploading: '上传中',
          paused: '暂停中',
          waiting: '等待中'
        }
      }
    },
    methods: {
      // 上传完成
      complete() {
        console.log('complete', arguments)
      },
      // 一个根文件（文件夹）成功上传完成。
      fileComplete() {
        console.log('file complete', arguments)
        const file = arguments[0].file;
        axios.post('/boot/uploader/mergeFile', qs.stringify({
          filename: file.name,
          identifier: arguments[0].uniqueIdentifier,
          totalSize: file.size,
          type: file.type
        })).then(function (response) {
          console.log(response);
        }).catch(function (error) {
          console.log(error);
        });
      }
    },
    mounted() {
      this.$nextTick(() => {
        window.uploader = this.$refs.uploader.uploader
      })
    }
  }
</script>

<style>
  .uploader-example {
    width: 1000px;
    padding: 15px;
    margin: 40px auto 0;
    font-size: 12px;
    box-shadow: 0 0 10px rgba(62, 146, 203, 0.4);
  }

  .uploader-example .uploader-btn {
    margin-right: 4px;
  }

  .uploader-example .uploader-list {
    max-height: 440px;
    overflow: auto;
    overflow-x: hidden;
    overflow-y: auto;
  }
</style>
