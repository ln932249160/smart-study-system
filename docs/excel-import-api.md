# 用户 Excel 导入/模板下载 API

## 1. 下载导入模板

生成一个空的 Excel 文件，表头已填好，用户只需要按格式填数据。

```
GET /user/template
```

**响应：** 二进制文件流，Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

**Excel 表头：**

| 列 | 字段 | 说明 |
|----|------|------|
| 姓名 | name | 必填 |
| 角色 | role | 必填，2=班长 3=学生 |
| 性别 | gender | 选填，0=未知 1=男 2=女 |
| 手机号 | phone | 必填，同时作为登录账号 |
| 邮箱 | email | 选填 |
| 描述 | description | 选填 |
| 班级ID | classId | 选填，数字 |
| 账号 | account | 选填，不填默认=手机号 |

**前端调用：**

```javascript
fetch('/user/template', {
  headers: { 'Authorization': 'Bearer ' + token }
})
.then(res => res.blob())
.then(blob => {
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '用户导入模板.xlsx'
  a.click()
})
```

---

## 2. 批量导入用户

上传用户填好的 Excel 文件，服务端逐行解析并入库。

```
POST /user/import
Content-Type: multipart/form-data
```

**请求参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| file | File | Excel 文件，支持 .xlsx / .xls |

**成功响应（code=200）：**

```json
{
  "code": 200,
  "message": "导入完成",
  "data": {
    "success": 8,
    "fail": 2,
    "errors": [
      "行3: 手机号为空，跳过",
      "行7: 账号已存在 13800138001，跳过"
    ]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| success | int | 成功导入数 |
| fail | int | 失败数 |
| errors | string[] | 失败原因，按 Excel 行号标注 |

**业务规则：**

- 权限：仅 teacher（老师）可操作，其他角色返回 403
- 默认密码：`123456`（BCrypt 加密）
- 账号：填了用填的，没填默认用手机号
- 状态：默认 1（正常）
- 账号去重：已存在的账号跳过，不会覆盖

**前端调用：**

```javascript
const formData = new FormData()
formData.append('file', fileInput.files[0])

fetch('/user/import', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer ' + token },
  body: formData
})
.then(res => res.json())
.then(json => {
  // json.data.success  成功数
  // json.data.fail     失败数
  // json.data.errors   失败明细
})
```

---

## 导入流程

```
下载模板 → 填写数据 → 上传文件 → 展示结果
                          │
                          ├── 成功 → 用户已入库
                          └── 失败 → 查看 errors 修正后重新上传
```
