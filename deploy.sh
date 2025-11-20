#!/bin/bash

# 一键部署脚本
# 编译Java后端、管理后台和uniapp，并将编译后的文件放到public目录下

set -e  # 遇到错误立即退出

echo "=========================================="
echo "开始一键部署..."
echo "=========================================="

# 创建public目录结构
PUBLIC_DIR="public"
JAR_DIR="${PUBLIC_DIR}/jar"
ADMIN_DIR="${PUBLIC_DIR}/admin"
MOBILE_DIR="${PUBLIC_DIR}/mobile"

# 清理旧的构建文件
echo "清理旧的构建文件..."
rm -rf ${PUBLIC_DIR}
mkdir -p ${JAR_DIR} ${ADMIN_DIR} ${MOBILE_DIR}

# 1. 编译Java后端
echo ""
echo "=========================================="
echo "1. 编译Java后端..."
echo "=========================================="
cd novel-api
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "Java后端编译失败！"
    exit 1
fi

# 复制jar文件到public/jar目录
JAR_FILE=$(find target -name "*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "未找到jar文件！"
    exit 1
fi
cp ${JAR_FILE} ../${JAR_DIR}/novel-api.jar
echo "Java后端编译完成: ${JAR_FILE} -> ${JAR_DIR}/novel-api.jar"
cd ..

# 2. 编译管理后台
echo ""
echo "=========================================="
echo "2. 编译管理后台..."
echo "=========================================="
cd novel-admin

# 检查node_modules是否存在
if [ ! -d "node_modules" ]; then
    echo "安装依赖..."
    npm install
fi

# 构建项目
npm run build
if [ $? -ne 0 ]; then
    echo "管理后台编译失败！"
    exit 1
fi

# 复制构建后的文件到public/admin目录
cp -r dist/* ../${ADMIN_DIR}/
echo "管理后台编译完成: dist/* -> ${ADMIN_DIR}/"
cd ..

# 3. 编译uniapp (H5版本)
echo ""
echo "=========================================="
echo "3. 编译uniapp (H5版本)..."
echo "=========================================="
cd novel-app

# 检查是否有uni-app CLI
UNI_CLI_FOUND=false
if command -v uni &> /dev/null; then
    UNI_CLI_FOUND=true
    echo "检测到uni-app CLI"
elif [ -f "package.json" ]; then
    # 检查本地是否有uni-app相关依赖
    if grep -q "@dcloudio" package.json 2>/dev/null; then
        echo "检测到本地uni-app依赖，尝试使用npm构建..."
        if [ ! -d "node_modules" ]; then
            npm install
        fi
        # 尝试使用npm scripts构建
        if grep -q '"build:h5"' package.json 2>/dev/null; then
            npm run build:h5
            UNI_CLI_FOUND=true
        elif grep -q '"build"' package.json 2>/dev/null; then
            npm run build -- -p h5
            UNI_CLI_FOUND=true
        fi
    fi
fi

# 查找构建输出目录
H5_BUILD_DIR=""
if [ "$UNI_CLI_FOUND" = true ]; then
    # 常见的构建输出目录
    for dir in "dist/build/h5" "dist/h5" "dist" "unpackage/dist/build/h5" "unpackage/h5"; do
        if [ -d "$dir" ] && [ -f "$dir/index.html" ]; then
            H5_BUILD_DIR="$dir"
            break
        fi
    done
fi

if [ -n "$H5_BUILD_DIR" ]; then
    cp -r ${H5_BUILD_DIR}/* ../${MOBILE_DIR}/
    echo "uniapp编译完成: ${H5_BUILD_DIR}/* -> ${MOBILE_DIR}/"
else
    echo "=========================================="
    echo "警告: 未找到uni-app构建工具或构建输出"
    echo ""
    echo "请使用以下方式之一构建H5版本："
    echo ""
    echo "方式1: 使用HBuilderX"
    echo "  1. 打开HBuilderX"
    echo "  2. 导入项目: novel-app目录"
    echo "  3. 发行 -> 网站-H5 -> 发行"
    echo "  4. 构建完成后，将输出目录内容复制到: ${MOBILE_DIR}/"
    echo ""
    echo "方式2: 使用uni-app CLI"
    echo "  1. 安装: npm install -g @dcloudio/uni-cli"
    echo "  2. 在novel-app目录运行: uni build -p h5"
    echo "  3. 构建完成后，将输出目录内容复制到: ${MOBILE_DIR}/"
    echo ""
    echo "方式3: 使用本地npm（如果已配置）"
    echo "  1. 在novel-app目录运行: npm install"
    echo "  2. 运行构建命令（根据package.json配置）"
    echo "  3. 将输出目录内容复制到: ${MOBILE_DIR}/"
    echo ""
    echo "注意: manifest.json已配置base路径为 /mobile/"
    echo "构建时请确保使用此配置"
    echo "=========================================="
fi
cd ..

echo ""
echo "=========================================="
echo "部署完成！"
echo "=========================================="
echo "构建结果："
echo "  - Java后端: ${JAR_DIR}/novel-api.jar"
echo "  - 管理后台: ${ADMIN_DIR}/"
echo "  - 移动端: ${MOBILE_DIR}/"
echo ""
echo "部署说明："
echo "1. 将public目录下的所有文件部署到Web服务器"
echo "2. 配置Web服务器路由："
echo "   - /api/* -> 后端API服务 (运行jar文件)"
echo "   - /admin/* -> 管理后台静态文件"
echo "   - /mobile/* -> 移动端H5静态文件"
echo "   - / -> 可以指向移动端或管理后台"
echo "3. 启动Java后端："
echo "   java -jar ${JAR_DIR}/novel-api.jar"
echo "=========================================="
