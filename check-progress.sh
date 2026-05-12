#!/bin/bash
# ERP 项目进度检查脚本
# 用法: bash check-progress.sh

PLAN="docs/superpowers/plans/2026-05-12-erp-implementation.md"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$PROJECT_DIR"

echo "========== ERP 项目进度 =========="
echo "时间: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 总 Task 数
TOTAL=$(grep -c '^### Task' "$PLAN" 2>/dev/null || echo "?")
echo "总任务数: $TOTAL"

# 最后一次提交
LAST_COMMIT=$(git log --oneline -1 --format="%s")
LAST_TIME=$(git log --oneline -1 --format="%ai")
echo "最新提交: $LAST_COMMIT"
echo "提交时间: $LAST_TIME"
echo ""

# 总提交数
COMMITS=$(git log --oneline --format="%h" | wc -l)
echo "总提交数: $COMMITS"

# 未提交的变更
CHANGES=$(git status --short | wc -l)
if [ "$CHANGES" -gt 0 ]; then
  echo "未提交文件: $CHANGES 个（正在工作中）"
  git status --short
else
  echo "工作区干净（可能刚提交完或等待中）"
fi

echo ""
echo "--- 各阶段提交情况 ---"
declare -A phases=(
  ["脚手架+系统管理"]="erp-common|erp-system|erp-server|parent.POM"
  ["采购管理"]="purchase"
  ["库存管理"]="inventory"
  ["销售管理"]="sales"
  ["财务管理"]="finance"
  ["生产管理"]="production"
  ["前端+仪表盘"]="frontend|Docker|仪表"
)

for phase in "脚手架+系统管理" "采购管理" "库存管理" "销售管理" "财务管理" "生产管理" "前端+仪表盘"; do
  keywords="${phases[$phase]}"
  count=$(git log --oneline --grep="$keywords" 2>/dev/null | wc -l)
  bar=""
  [ "$count" -gt 0 ] && bar="$(printf '%*s' "$count" | tr ' ' '#')"
  printf "%-20s %s (%d)\n" "$phase" "$bar" "$count"
done

echo ""
echo "--- 进程状态 ---"
if tasklist 2>/dev/null | grep -q "claude"; then
  echo "Claude 进程: 运行中"
else
  echo "Claude 进程: 未运行"
fi
