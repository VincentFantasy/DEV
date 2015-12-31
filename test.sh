git filter-branch -f --env-filter '

CORRECT_NAME="VincentFantasy"
CORRECT_EMAIL="892942765@qq.com"
echo 替换COMMITTER Email "$CORRECT_NAME" "$CORRECT_EMAIL"
export GIT_COMMITTER_NAME="$CORRECT_NAME"
export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
echo 替换AUTHOR Email 为 "$CORRECT_NAME" "$CORRECT_EMAIL"
export GIT_AUTHOR_NAME="$CORRECT_NAME"
export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
' --tag-name-filter cat -- --branches --tags
