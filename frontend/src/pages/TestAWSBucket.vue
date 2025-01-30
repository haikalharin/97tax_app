<template>
  <page :navType="'bucket-testing'">
    <div class="d-flex flex-column m-auto pt-5 pb-5" style="width: 600px;">
        <p class="text-center">S3 Bucket Testing Page</p>
        <button type="button" class="btn btn-green mt-3 w-100 font-weight-bold text-uppercase btn-secondary" @click="proceedPayment()">Upload PDF</button>
        <button type="button" class="btn btn-green mt-3 w-100 font-weight-bold text-uppercase btn-secondary" @click="download()">DownloadPDF</button>
    </div>
  </page>
  
</template>

<script>
    import { AXIOS } from '../scripts/http-common';
export default {
    name: "TestAWSBucket",
    methods: {
        proceedPayment() {
            AXIOS.post('/admin/uploadFileToS3', {}).then(res=>{
                console.log(res);
            })
            // console.log('here');
        },
        download() {
            window.location.href = "/api/admin/downloadFileFromS3";
        }
    }
}
</script>
